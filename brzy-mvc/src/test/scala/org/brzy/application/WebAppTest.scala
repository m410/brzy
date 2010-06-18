package org.brzy.application

import org.junit.Assert._

import org.brzy.config.webapp.WebAppConfig
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig
import org.brzy.mock.MockPluginConfig
import org.junit.{Ignore, Test}


class WebAppTest extends JUnitSuite {

  class MockWebApp(config:WebAppConfig) extends WebApp(config) 

  @Test
  @Ignore
  def testCreate = {

    val view = new MockPluginConfig(Map[String,AnyRef]()) {
      val fileExtension = ".ssp"
      override val resourceClass = Option("org.brzy.mock.MockPluginResource")
    }
    val boot = new BootConfig(Map[String,AnyRef](
      "environment" -> "development"
      ))
    val config = new WebAppConfig(boot,view,Nil,Nil)
    val webapp = new MockWebApp(config)
    assertNotNull(webapp)
    assertNotNull(webapp.services)
    assertEquals(0, webapp.services.size)
    assertNotNull(webapp.controllers)
    assertEquals(0, webapp.controllers.size)
    assertNotNull(webapp.actions)
    assertEquals(0,webapp.actions.size)

  }
}