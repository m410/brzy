package org.brzy.application

import org.junit.Test
import org.junit.Assert._

import org.brzy.interceptor.ProxyFactory._
import org.brzy.interceptor.MethodInvoker
import org.brzy.interceptor.impl.LoggingInterceptor
import org.brzy.mock.{UserService, UserController}
import org.brzy.config.webapp.WebAppConfig
import org.scalatest.junit.JUnitSuite
import org.brzy.config.plugin.Plugin
import org.brzy.config.common.BootConfig

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebAppTest extends JUnitSuite {

  class MockWebApp(config:WebAppConfig) extends WebApp(config) 

  @Test
  def testCreate = {

    val view = new Plugin(Map[String,AnyRef]()) {
      val fileExtension = ".ssp"
    }
    val boot = new BootConfig(Map[String,AnyRef](
      "environment" -> "development"
      ))
    val config = new WebAppConfig(boot,view,Nil,Nil)
    val webapp = new MockWebApp(config)
    assertNotNull(webapp)
    assertNotNull(webapp.services)
    assertEquals(1, webapp.services.size)
    assertNotNull(webapp.controllers)
    assertEquals(1, webapp.controllers.size)
    assertNotNull(webapp.actions)
    assertEquals(8,webapp.actions.size)

  }
}