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
package org.brzy.tomcat

import java.io.{StringWriter, PrintWriter, File}
import tools.nsc.util.{FakePos, NoPosition, Position}
import scala.util.parsing.input.OffsetPosition
import tools.nsc.reporters.ConsoleReporter
import tools.nsc.{Settings, Global}

@deprecated
class ScalaCompiler(codeDir:File, outputDir:File, classpath:String) {

  private val settings = {
    codeDir.mkdirs
    val pathSeparator = File.pathSeparator
    val settings = new Settings(errorHandler)
    settings.classpath.value = classpath
    settings.outdir.value = codeDir.toString
    settings.deprecation.value = true
    settings.unchecked.value = true
    settings
  }

  private val compiler = new Global(settings, null)

  def compile(file:File) = {
    println(" -- re-compile")
    val messageCollector = new StringWriter
    val messageCollectorWrapper = new PrintWriter(messageCollector)

    var messages = List[CompilerError]()
    val reporter = new ConsoleReporter(settings, Console.in, messageCollectorWrapper) {
      override def printMessage(posIn: Position, msg: String) {
        val pos =
          if (posIn eq null)
            NoPosition
          else if (posIn.isDefined)
            posIn.inUltimateSource(posIn.source)
          else
            posIn

        pos match {
          case FakePos(fmsg) =>
            super.printMessage(posIn, msg);
          case NoPosition =>
            super.printMessage(posIn, msg);
          case _ =>
            messages = CompilerError(posIn.source.file.file.getPath, msg,
                OffsetPosition(posIn.source.content, posIn.point)) :: messages
            super.printMessage(posIn, msg);
        }
      }
    }
    compiler.reporter = reporter
    (new compiler.Run).compile(List(file.getCanonicalPath))

    if (reporter.hasErrors) {
      reporter.printSummary
      messageCollectorWrapper.close
      throw new CompilerException("Compilation failed: " + messageCollector +" - "+ messages)
    }
  }

  private def errorHandler(message: String): Unit = throw new CompilerException("Compilation failed: " + message)

}