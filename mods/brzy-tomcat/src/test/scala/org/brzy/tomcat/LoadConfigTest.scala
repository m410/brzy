package org.brzy.tomcat

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.webapp.ConfigFactory
import org.brzy.config.plugin.Plugin


class LoadConfigTest extends JUnitSuite {
  @Test def testLoadConfig = {
    val plugin = new Plugin(Map(
      "name" -> "brzy-tomcat",
      "version" -> "0.2",
      "org" -> "org.brzy"))
    val tomcat: Plugin = ConfigFactory.makeRuntimePlugin(plugin)
    assertTrue(tomcat.isInstanceOf[Plugin])
  }
}