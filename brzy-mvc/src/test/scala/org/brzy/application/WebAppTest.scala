package org.brzy.application

import org.junit.Test
import org.junit.Assert._

import org.brzy.interceptor.ProxyFactory._
import org.brzy.interceptor.MethodInvoker
import org.brzy.interceptor.impl.LoggingInterceptor
import org.brzy.mock.{UserService, UserController}
import org.brzy.config.BootConfig
import org.brzy.webapp.WebAppConfig

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebAppTest {

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
//    val config = new AppConfig()
////    config.views = new Views
//    config.views.html_version = "html5"
//    config.views.file_extension = ".jsp"
//    val webapp = new MockWebApp(config)
//    assertNotNull(webapp)
//    assertNotNull(webapp.services)
//    assertEquals(1, webapp.services.size)
//    assertNotNull(webapp.controllers)
//    assertEquals(1, webapp.controllers.size)
//    assertNotNull(webapp.actions)
//    assertEquals(8,webapp.actions.size)

  }
}