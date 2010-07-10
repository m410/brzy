package org.brzy.tomcat

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.webapp.ConfigFactory
import org.brzy.config.mod.Mod


class LoadConfigTest extends JUnitSuite {
  @Test def testLoadConfig = {
    val mod = new Mod(Map(
      "name" -> "brzy-tomcat",
      "version" -> "0.2",
      "org" -> "org.brzy"))
    val tomcat: Mod = ConfigFactory.makeRuntimeModule(mod)
    assertTrue(tomcat.isInstanceOf[Mod])
  }
}