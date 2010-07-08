package org.brzy.shell

import org.junit.Test
import org.junit.Assert._
import org.brzy.config._
import mod.Mod
import org.brzy.webapp.ConfigFactory._
import java.io.File
import org.scalatest.junit.JUnitSuite

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class LogBackXmlTest extends JUnitSuite {
  @Test
  def testCreate = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val bootConfig = makeBootConfig(new File(url.getFile), "test")
    val logback = new LogBackXml(bootConfig)
    println(logback.body)
    assertNotNull(logback.body)
  }
}
