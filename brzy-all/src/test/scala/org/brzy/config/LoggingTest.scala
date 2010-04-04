package org.brzy.config

import org.junit.Test

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class LoggingTest {

  @Test
  def testLogging = {
    val log = new Logging()
    log.provider = "log4j"
    log.appenders = Array("file ch.qos.logback.core.ConsoleAppender layout:ch.qos.logback.classic.PatternLayout")
    log.loggers = Array("debug org.apache ")
    log.root = Array("info file")
  }
}