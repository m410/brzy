package org.brzy.fab.dependency

import org.apache.ivy.core.IvyContext
import org.brzy.fab.print.{Conversation, Info, Warn, Debug}
import org.apache.ivy.util.MessageLoggerEngine
import org.apache.ivy.Ivy.IvyCallback
import org.apache.ivy.{Ivy=>JIvy}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

object Ivy {

  def doInIvyCallback(callback: (JIvy, IvyContext) => java.lang.Object)(implicit line: Conversation) = {
    val ivy = new JIvy() {
      val logEngine = new MessageLoggerEngine {
        override def error(p1: String) = line.endWithError(p1)
        override def warn(p1: String) = line.say(Warn(p1))
        override def rawinfo(p1: String) = line.say(Debug(p1))
        override def info(p1: String) = line.say(Info(p1))
        override def deprecated(p1: String) = line.say(Warn(p1))
        override def verbose(p1: String) = line.say(Debug(p1))
        override def debug(p1: String) = line.say(Debug(p1))
      }

      override def getLoggerEngine = logEngine
    }
    ivy.bind
    ivy.execute(new IvyCallback() {
      def doInIvyContext(ivy: JIvy, context: IvyContext): java.lang.Object = {
        callback(ivy, context)
      }
    })
  }
}