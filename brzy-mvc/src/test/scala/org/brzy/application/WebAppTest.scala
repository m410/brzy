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

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebAppTest extends JUnitSuite {

  class MockWebApp(config:WebAppConfig) extends WebApp(config) {
    override val services = Array(
      make(classOf[UserService],new MethodInvoker with LoggingInterceptor)
      )
    override val controllers = Array(
      make(classOf[UserController],new MethodInvoker with LoggingInterceptor)
      )
  }

  @Test
  def testCreate = {

    val view = new Plugin(Map[String,AnyRef]()) {
      val fileExtension = ".ssp"
    }
    val config = new WebAppConfig(null,view,Nil,Nil)
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