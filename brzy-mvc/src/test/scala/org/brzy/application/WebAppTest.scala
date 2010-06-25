package org.brzy.application

import org.junit.Assert._

import org.brzy.config.webapp.WebAppConfig
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig
import org.brzy.mock.MockPluginConfig
import org.junit.Test


class WebAppTest extends JUnitSuite {

  class MockWebApp(config: WebAppConfig) extends WebApp(config)


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
    val webapp = new MockWebApp(config)
    assertNotNull(webapp)
    assertNotNull(webapp.services)
    assertEquals(1, webapp.services.size)
    assertNotNull(webapp.controllers)
    assertEquals(1, webapp.controllers.size)
    assertNotNull(webapp.actions)
    assertEquals(14, webapp.actions.size)

  }
}