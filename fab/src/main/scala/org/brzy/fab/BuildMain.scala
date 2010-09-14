/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.fab


import build.{BuildContext, ArchetypeDatabase}
import print._
import file._
import module.ModuleResolver
import config.LoadInitConfig
import org.apache.commons.cli._

import io.Source
import scala.tools.nsc.{Interpreter, GenericRunnerSettings, Settings}
import scala.Option

/**
 * This runs the main build script.  Bassed on the configuration file in the current directory
 * it loads the archetype for the build file and executes the task passed in as an
 * argument.  If not arguments at set it calls the default task.
 *
 * @author Michael Fortin
 */
object BuildMain {
  val options = {
    val options = new Options
    options.addOption("help", false, "Print this message.")
    options.addOption("version", false, "output the version")
    options.addOption("tasks", false, "List available tasks, phases and plugins.")
    options.addOption("verbose", false, "Verbose output.")
    options.addOption("debug", false, "Debug output.")
    options.addOption("env", true, "Set the environment.  Override the default.")
    options
  }

  def main(args: Array[String]): Unit = {
    val parser = new PosixParser
    val cmd = parser.parse(options, args)

    if (cmd.hasOption("help")) {
      val formatter = new HelpFormatter
      formatter.printHelp("b2 [options] [actions]", options)
      exit(0)
    }

    if (cmd.hasOption("version")) {
      println("Brzy Fab(ricate) Version: 0.2")
      println("Brzy Fab(ricate) Home: " + System.getenv("BRZY_HOME"))
      println("Java: " + System.getProperty("java.vm.name") + " ( build " + System.getProperty("java.runtime.version") + ")")
      println("Java Home: " + System.getenv("JAVA_HOME"))
      println("Scala: " + util.Properties.versionString)
      exit(0)
    }


    val talk = new Conversation(cmd.hasOption("debug") || cmd.hasOption("verbose"), cmd.hasOption("debug"))
    implicit val iTalk = talk

    talk.begin(if (cmd.getArgs.length > 0) cmd.getArgs() else Array("default"))
    val workingDir = System.getProperty("user.dir")
    talk.say(Info("working directory: " + workingDir))

    val brzyHome = System.getenv("BRZY_HOME")
    talk.say(Info("brzy home directory: " + brzyHome))

    val configFile = File("*.b.yml")
    val archetypeName = configFile.getName.substring(0, configFile.getName.length - 6)
    val description = ArchetypeDatabase.list.find(desc => desc.name == archetypeName) match {
      case Some(e) => e
      case _ => error("No Archetype Found by name: " + archetypeName)
    }

    val defaultPath = System.getenv("BRZY_HOME") + "/archetypes/" +archetypeName+"/" +archetypeName+".default.b.yml"
    val defaultConfigFile = File(defaultPath)
    talk.say(Info("default config: " + defaultConfigFile.getAbsolutePath))
    talk.say(Info("app config: " + configFile.getAbsolutePath))

    talk.subject(Phase("init")) {
      talk.subject(Task("load-mod")) {
        val defaultConfig = LoadInitConfig(defaultConfigFile)
        val appConfig = LoadInitConfig(configFile)
        ModuleResolver(defaultConfig << appConfig)
      }
    }
    val archetypeClasspath = Classpath(description.baseDir.listFiles.map(_.getAbsolutePath).toList)
    var settings = new GenericRunnerSettings(str => talk.say(Info(str)))
    val moduleClasspath = Classpath(Files(".brzy/modules/*/*.jar").map(_.getAbsolutePath))
    val pluginClasspath = makePluginClasspath
    talk.subject(Phase("init")) {
      talk.subject(Task("load-deps")) {
       loadBuildDependencies(archetypeClasspath :: moduleClasspath :: pluginClasspath :: Nil, settings)
      }
    }

    var interpreter = new Interpreter(settings, talk.out)
    talk.subject(Phase("init")) {
      talk.subject(Task("defaults")) {
        loadDefaults(interpreter)
      }
      talk.subject(Task("env")) {
        loadEnv(interpreter, cmd)
      }
      talk.subject(Task("plugins")) {
        loadPlugins(interpreter)
      }
    }

    interpreter.interpret("import " + description.className)
    interpreter.interpret("val arch = new " + description.simpleName + "(context,actions,plugins.toList)")

    interpreter.interpret("try {")
    if (cmd.hasOption("tasks"))
      interpreter.interpret("arch.taskInfo")
    else
      interpreter.interpret("arch.build(args)")
    interpreter.interpret("} catch { case e:Exception => line.endWithError(e) }")
    talk.end
    interpreter.close
  }

  protected[fab] def makePluginClasspath = {
    // todo this also needs to load dependencies from plugins
    // todo plugin libs needs to be declared in a yml file
    Classpath(List[String]())
  }

  protected[fab] def loadBuildDependencies(cp: List[Classpath], settings: Settings)(implicit talk: Conversation) = {
    talk.say(Debug("setup interpreter dependencies"))
    settings.classpath.value = ""
    val cli = File(System.getenv("BRZY_HOME") + "/lib/cli-0.1.jar")

    settings.classpath.append(cli.getAbsolutePath)
    talk.say(Debug("cp: " + cli.getAbsolutePath))
    cp.foreach(_.paths.foreach(path => {
      settings.classpath.append(path)
      talk.say(Debug("cp: " + path))
    }))
  }

  protected[fab] def loadDefaults(interpreter: Interpreter)(implicit talk: Conversation): Unit = {
    talk.say(Debug("setup defaults"))
    interpreter.interpret("import org.brzy.fab.print._")
    interpreter.interpret("import org.brzy.fab.plugin._")
    interpreter.interpret("import org.brzy.fab.phase._")
    interpreter.interpret("import org.brzy.fab.task._")
    interpreter.interpret("import org.brzy.fab.build.BuildContext")
  }

  protected[fab] def loadEnv(interpreter: Interpreter, cmd: CommandLine)(implicit talk: Conversation) = {
    talk.say(Debug("setup env"))
    interpreter.bind("args", "Array[String]", cmd.getArgs)

    val env = if (cmd.hasOption("env")) cmd.getOptionValue("env") else "development"
    interpreter.bind("context", "org.brzy.fab.build.BuildContext", BuildContext(env, talk))

    val actions = if (cmd.getArgs.length > 0) Option(cmd.getArgs()) else Option(null)
    interpreter.bind("actions", "Option[Array[String]]", actions)

    interpreter.bind("line", "org.brzy.fab.print.Conversation", talk)
    interpreter.interpret("implicit val iLine = line")
  }

  protected[fab] def loadPlugins(interpreter: Interpreter)(implicit talk: Conversation) = {
    interpreter.interpret("val plugins = collection.mutable.ListBuffer[Class[_<:BuildPlugin]]()")
    val scriptsDir = Directory("scripts")

    scriptsDir.listFiles.foreach(script => {
      if (script.getName.endsWith("Plugin.scala")) {
        interpreter.interpret(Source.fromFile(script).mkString)
        val className = script.getName.substring(0, script.getName.length - 6)
        interpreter.interpret("plugins += classOf[" + className + "]")
        talk.say(Debug("add script: " + className))
      }
    })
  }
}