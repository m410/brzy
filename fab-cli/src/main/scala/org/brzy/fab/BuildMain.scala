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

import conf._
import mod.ModConf
import print._
import file._
import build.{BuildContext, ArchetypeDatabase}
import plugin.PluginResolver
import module.ModuleResolver
import org.apache.commons.cli._

import java.io.{File => JFile}
import io.Source
import scala.tools.nsc.{Interpreter, GenericRunnerSettings, Settings}
import scala.Option
import collection.mutable.ListBuffer

/**
 * This runs the main build script.  Based on the configuration file in the current directory
 * it loads the archetype for the build file and executes the task passed in as an
 * argument.  If not arguments at set it calls the default task.
 *
 * @author Michael Fortin
 */
object BuildMain {
  val options = {
    val options = new Options
    options.addOption("help", false, "Print this message.")
    options.addOption("version", false, "Output the version information.")
    options.addOption("tasks", false, "List available tasks, phases and plugins.")
    options.addOption("v", "verbose", false, "Verbose output.")
    options.addOption("d", "debug", false, "Debug output.")
    options.addOption("e", "env", true, "Set the environment.  Override the default environment of development.")
    options.addOption("m", "mods", false, "Reload build modules.")
    options
  }

  def main(args: Array[String]): Unit = {
    val parser = new PosixParser
    val cmd = parser.parse(options, args)

    if (cmd.hasOption("help")) {
      val formatter = new HelpFormatter
      formatter.printHelp("fab [options] [tasks]", options)
      exit(0)
    }

    if (cmd.hasOption("version")) {
      println("Brzy Fab(ricate) Version: 0.2")
      println("Brzy Home: " + System.getenv("BRZY_HOME"))
      println("Java: " + System.getProperty("java.vm.name") + " ( build " + System.getProperty("java.runtime.version") + ")")
      println("Java Home: " + System.getenv("JAVA_HOME"))
      println("Scala: " + util.Properties.versionString)
      exit(0)
    }


    val talk = new Conversation(cmd.hasOption("debug") || cmd.hasOption("verbose"), cmd.hasOption("debug"))
    implicit val iTalk = talk

    talk.begin(if (cmd.getArgs.length > 0) cmd.getArgs() else Array("default"))
    val workingDir = System.getProperty("user.dir")

    val brzyHomeStr = System.getenv("BRZY_HOME")
    val brzyHome = new JFile(brzyHomeStr)

    talk.subject(Task("init")) {
      talk.say(Info("working directory: " + workingDir))
      talk.say(Info("brzy home directory: " + brzyHomeStr))
    }

    val configFile = File("*.b.yml")
    val archName = configFile.getName.substring(0, configFile.getName.length - 6)
    val archDesc = ArchetypeDatabase.list.find(desc => desc.name == archName) match {
      case Some(e) => e
      case _ => error("No Archetype Found by name: " + archName)
    }

    val defaultConfigFile = File(brzyHome, "/archetypes/" + archName + "/" + archName + ".default.b.yml")

    talk.subject(Task("init")) {
      talk.say(Info("default config: " + defaultConfigFile.getAbsolutePath))
      talk.say(Info("app config: " + configFile.getAbsolutePath))
    }

    if (!File(".brzy/modules").exists || cmd.hasOption("mods")) {
      talk.subject(Task("install-mod")) {
        val defaultConf = new ModConf(Yaml(defaultConfigFile))
        val appConf = new ModConf(Yaml(configFile))
        ModuleResolver({defaultConf << appConf}.asInstanceOf[ModConf])
      }
    }

    val cpath = archDesc.baseDir.listFiles.filter(_.getName.endsWith(".jar"))
    val archetypeClasspath = Classpath(cpath.map(_.getAbsolutePath).toList)
    var settings = new GenericRunnerSettings(str => talk.say(Info(str)))
    val moduleClasspath = Classpath(Files(".brzy/modules/*/*.jar").filter(_.getName.endsWith(".jar")).map(_.getAbsolutePath))
    val pluginClasspath = makePluginClasspath

    talk.subject(Task("load-deps")) {
      loadBuildDependencies(archetypeClasspath :: moduleClasspath :: pluginClasspath :: Nil, settings)
    }

    var interpreter = new Interpreter(settings, talk.out)
    talk.subject(Task("init-defaults")) {
      loadDefaults(interpreter)
    }
    talk.subject(Task("init-env")) {
      loadEnv(interpreter, cmd)
    }
    talk.subject(Task("load-plugins")) {
      loadPlugins(interpreter)
    }

    interpreter.interpret("import " + archDesc.className)
    interpreter.interpret("val arch = new " + archDesc.simpleName + "(context,actions,plugins.toList)")

    if (cmd.hasOption("tasks"))
      interpreter.interpret("arch.taskInfo")
    else
      interpreter.interpret("arch.build(args)")

    talk.end
    interpreter.close
  }

  /**
   * This is a temporary hack job.  Running tomcat as a regular plugin doesn't work because
   * of interpreter classloader issues. Once those are resolved, this will be removed.
   */
  protected[fab] def tomcat6 = {
    // TODO add custom tomcat plugin
  }

  protected[fab] def loadBuildDependencies(cp: List[Classpath], settings: Settings)(implicit talk: Conversation) = {
    talk.say(Debug("setup interpreter dependencies"))
    settings.classpath.value = ""

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
    val scriptsDir = File("scripts")

    if (scriptsDir.exists) {
      scriptsDir.listFiles.foreach(script => {
        if (script.getName.endsWith("Plugin.scala")) {
          interpreter.interpret(Source.fromFile(script).mkString)
          val className = script.getName.substring(0, script.getName.length - 6)
          interpreter.interpret("plugins += classOf[" + className + "]")
          talk.say(Info("add script: " + className))
        }
      })
    }

    val plugins = Files(".brzy/modules/*/scripts/*Plugin.scala")
    plugins.foreach(script => {
      interpreter.interpret(Source.fromFile(script).mkString)
      val className = script.getName.substring(0, script.getName.length - 6)
      interpreter.interpret("plugins += classOf[" + className + "]")
      talk.say(Info("add plugin: " + className))
    })
  }

  protected[fab] def makePluginClasspath()(implicit talk: Conversation) = {

    if (!File(".brzy/plugins").exists) {
      val buffer = new ListBuffer[BaseConf]()
      val scriptsDir = File("scripts")
      
      if (scriptsDir.exists) {
        scriptsDir.listFiles.foreach(script => {
          if (script.getName.endsWith("Dependencies.b.yml")) {
            buffer += new BaseConf(Yaml(script))
            talk.say(Info("add plugin deps: " + script))
          }
        })
      }

      val plugins = Files(".brzy/modules/*/scripts/*Dependencies.b.yml")
      plugins.foreach(script => {
        buffer += new BaseConf(Yaml(script))
        talk.say(Info("add plugin deps: " + script))
      })
			val starter = new BaseConf(Map[String,AnyRef]())
      val baseConfig = buffer.foldLeft(starter)((a,b) => a << b)
      PluginResolver(baseConfig)
    }

    Classpath(Files(".brzy/plugins/*.jar").map(_.getAbsolutePath))
  }
}