package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config._
import collection.mutable.ArrayBuffer

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class LogBackXmlTest {
  @Test
  def testCreate = {
    val config = new WebappConfig(Map[String, AnyRef](
      "logging" -> Map[String, AnyRef](
        "appenders" -> ArrayBuffer[AnyRef](Map[String, String](
          "name" -> "STDOUT",
          "appenderClass" -> "ch.qos.logback.core.ConsoleAppender",
          "layout" -> "ch.qos.logback.classic.PatternLayout",
          "pattern" -> "%-4relative [%thread] %-5level %class - %msg%n")),
        "loggers" -> ArrayBuffer[AnyRef](Map[String, String](
          "name" -> "org.brzy",
          "level" -> "INFO")),
        "root" -> Map[String, AnyRef](
          "level" -> "INFO",
          "ref" -> Seq("STDOUT"))
        )
      ))

    val logback = new LogBackXml(config)
    println(logback.body)
    assertNotNull(logback.body)
  }
}
