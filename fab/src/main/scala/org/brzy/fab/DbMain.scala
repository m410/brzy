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

/**
 * Command line app to manage the archetype database.
 * 
 * @author Michael Fortin
 */
object DbMain {
  val options = {
		val options = new Options
		options.addOption("help",    false, "Print this message.")
		options
	}

	def main(args: Array[String]):Unit = {
		val parser = new PosixParser
		val cmd = parser.parse( options, args)

		if(cmd.hasOption("help")) {
			val formatter = new HelpFormatter
			formatter.printHelp("fab-db [options] [actions]", options)
			exit(0)
		}
  }
}