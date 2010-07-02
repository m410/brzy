package org.brzy.application

import org.junit.Assert._

import org.brzy.config.webapp.WebAppConfig
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig
import org.junit.Test
import org.brzy.mock.MockPluginConfig


class WebAppTest extends JUnitSuite {

  @Test
  def testCreate = {

    val view = new MockPluginConfig(Map[String, AnyRef](
      "fileExtension" -> ".ssp",
      "resource_class" -> "org.brzy.mock.MockPluginResource"
      ))
    val boot = new BootConfig(Map[String, AnyRef](
      "environment" -> "development",
      "application" -> Map(
        "name" -> "test",
        "org" -> "org.brzy.mock")
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