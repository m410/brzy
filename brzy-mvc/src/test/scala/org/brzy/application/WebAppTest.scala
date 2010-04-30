package org.brzy.application

import org.junit.Test
import org.junit.Assert._

import org.brzy.interceptor.ProxyFactory._
import org.brzy.interceptor.Proxy
import org.brzy.interceptor.impl.LoggingInterceptor
import org.brzy.mock.{UserService, UserController}
import org.brzy.config.{Views, AppConfig}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebAppTest {

  class MockWebApp(config:AppConfig) extends WebApp(config) {
    override val services = Array(
      make(classOf[UserService],new Proxy with LoggingInterceptor)
      )
    override val controllers = Array(
      make(classOf[UserController],new Proxy with LoggingInterceptor)
      )
  }

  @Test
  def testCreate = {
    val config = new AppConfig()
    config.views = new Views
    config.views.implementation  = ".jsp"
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