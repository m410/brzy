package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class LogBackXmlTest {

  @Test
  def testCreate = {
    val config = new Config()
    val logging: Logging = new Logging()

    val appender = new Appender()
    appender.name = "STDOUT"
    appender.appender_class = "ch.qos.logback.core.ConsoleAppender"
    appender.layout = "ch.qos.logback.classic.PatternLayout"
    appender.pattern  = "%-4relative [%thread] %-5level %class - %msg%n"
    logging.appenders = Array(appender)

    val logger = new Logger()
    logger.name = "org.brzy"
    logger.level = "INFO"
    logging.loggers = Array(logger)

    val root = new Root()
    root.level = "INFO"
    root.ref = Array("STDOUT")
    logging.root = root

    config.logging = logging

    val logback = new LogBackXml(config)
    assertNotNull(logback.body)
  }
}