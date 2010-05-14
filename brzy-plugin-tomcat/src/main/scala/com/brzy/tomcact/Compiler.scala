package org.brzy.tomcat

import java.io.{StringWriter, PrintWriter, File}
import tools.nsc.util.{FakePos, NoPosition, Position}
import scala.util.parsing.input.OffsetPosition
import tools.nsc.reporters.ConsoleReporter
import tools.nsc.{Settings, Global}

/**
 * Compile scala code
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class Compiler(codeDir:File, outputDir:File, classpath:String) {

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