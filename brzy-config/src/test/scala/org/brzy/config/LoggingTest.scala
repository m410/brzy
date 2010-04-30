package org.brzy.config

import org.junit.Test
import org.junit.Assert._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class LoggingTest {

  @Test
  def testLogging = {
    val log = new Logging()
    log.provider = "logback"

    val appender = new Appender
    log.appenders = Array(appender)

    val logger = new Logger
    log.loggers = Array(logger)

    log.root = new Root

    assertNotNull(log.root)
  }
}