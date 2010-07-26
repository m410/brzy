package org.brzy.application

import org.junit.Assert._

import org.brzy.config.webapp.WebAppConfig
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig
import org.junit.Test
import org.brzy.mvc.mock.MockModConfig


class WebAppTest extends JUnitSuite {

  @Test
  def testCreate = {
    val view = new MockModConfig(Map[String, AnyRef](
      "name" -> "brzy-scalate",
      "fileExtension" -> ".ssp",
      "resource_class" -> "org.brzy.mvc.mock.MockModResource"
      ))
    val boot = new BootConfig(Map[String, AnyRef](
      "environment" -> "development",
      "application" -> Map(
        "name" -> "test",
        "org" -> "org.brzy.mvc.mock")
      ))
    val config = new WebAppConfig(boot, view, Nil, Nil)
    val webapp = new WebApp(config)
    assertNotNull(webapp)
    assertNotNull(webapp.services)
    assertEquals(1, webapp.services.size)
    assertNotNull(webapp.controllers)
    assertEquals(2, webapp.controllers.size)
    assertNotNull(webapp.actions)
    assertEquals(17, webapp.actions.size)
  }
}