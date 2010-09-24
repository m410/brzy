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


import file.FileUtils._
import file.{Files, File}
import org.apache.commons.cli.{Options, PosixParser, HelpFormatter}
import print.{Info, Question, Conversation}
import java.io.{FileWriter, BufferedWriter, File => JFile}
//import org.clapper.scalasti.StringTemplateGroup

/**
 * Command line app to create new brzy projects.  The available project types come from
 * the brzy archetype database.
 * 
 * @author Michael Fortin
 */
object NewMain {
  val options = {
		val options = new Options
		options.addOption("help",    false, "Print this message.")
		options.addOption("version", false, "Output the version.")
		options
	}

	def main(args: Array[String]):Unit = {
		val parser = new PosixParser
		val cmd = parser.parse( options, args)

		if(cmd.hasOption("help")) {
			val formatter = new HelpFormatter
			formatter.printHelp("fab-new [options] [archetype]", options)
			exit(0)
		}

    if (cmd.hasOption("version")) {
      println("Brzy Fab(ricate) New Project, Version: 0.2")
      println("Brzy Home: " + System.getenv("BRZY_HOME"))
      println("Java: " + System.getProperty("java.vm.name") + " ( build " + System.getProperty("java.runtime.version") + ")")
      println("Java Home: " + System.getenv("JAVA_HOME"))
      println("Scala: " + util.Properties.versionString)
      exit(0)
    }

    val talk = new Conversation(cmd.hasOption("debug") || cmd.hasOption("verbose"), cmd.hasOption("debug"))
    implicit val iTalk = talk

    talk.begin(Array(""))
    // TODO this should be broken out into a scala script bundled with the archetype.
    // TODO the Conversation class needs to be updated to include formatting for this command.
//    val archetypeName = talk.ask(Question("archetype: "))
    val org = talk.ask(Question("org: "))
    val name = talk.ask(Question("name: "))
    val version = talk.ask(Question("version: "))
    val create = talk.ask(Question("create brzy-webapp ["+org+ ", "+ name +", "+ version+"] : " ))

    val dest = File(name)
    dest.mkdirs
    val brzyHome = new JFile(System.getenv("BRZY_HOME"))
    val sources = Files(brzyHome,"archetypes/brzy-webapp/project/*")
    sources.foreach(_.copyTo(dest))
//
//    val group = new StringTemplateGroup("brzy", File(brzyHome, "archetypes/brzy-webapp/templates"))
//    val homeControllerTemplate = group.template("HomeController-scala")
//    homeControllerTemplate.setAttribute("package", org)
//    homeControllerTemplate.setAttribute("name", name)
//    homeControllerTemplate.setAttribute("version", version)
//    val writer = new BufferedWriter(new FileWriter(File(name + "/src/scala/HomeController.scala")))
//    writer.write(homeControllerTemplate.toString)
//    writer.close
//
//    val webappTemplate = group.template("WebApp-scala")
//    webappTemplate.setAttribute("package", org)
//    webappTemplate.setAttribute("name", name)
//    webappTemplate.setAttribute("version", version)
//    val writer2 = new BufferedWriter(new FileWriter(File(name + "/src/scala/Application.scala")))
//    writer2.write(webappTemplate.toString)
//    writer2.close
//
//    val brzyWebappTemplate = group.template("brzy-webapp-yml")
//    brzyWebappTemplate.setAttribute("package", org)
//    brzyWebappTemplate.setAttribute("name", name)
//    brzyWebappTemplate.setAttribute("version", version)
//    val writer3 = new BufferedWriter(new FileWriter(File(name + "/brzy-webapp.b.yml")))
//    writer3.write(brzyWebappTemplate.toString)
//    writer3.close
//
//    talk.end
    exit(0)
  }
}