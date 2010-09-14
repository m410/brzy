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


import org.apache.commons.cli.{Options, PosixParser, HelpFormatter}
import print.{Info, Question, Conversation}

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
      println("Brzy Fab(ricate) Home: " + System.getenv("BRZY_HOME"))
      println("Java: " + System.getProperty("java.vm.name") + " ( build " + System.getProperty("java.runtime.version") + ")")
      println("Java Home: " + System.getenv("JAVA_HOME"))
      println("Scala: " + util.Properties.versionString)
      exit(0)
    }

    val talk = new Conversation(cmd.hasOption("debug") || cmd.hasOption("verbose"), cmd.hasOption("debug"))
    implicit val iTalk = talk

    talk.begin(Array(""))
    talk.say(Info("THIS IS NOT IMPLEMENTED YET. SORRY",true))
    // TODO hard-wire to the brzy-webapp archetype
    val archetypeName = talk.ask(Question("archetype: "))
    talk.say(Info("you picked: " + archetypeName,true))
    talk.end
    exit(0)
  }
}