package org.brzy.mvc.action

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web.{MockServletConfig, MockServletContext, MockHttpServletResponse, MockHttpServletRequest}
import org.brzy.application.WebApp
import org.brzy.config.webapp.WebAppConfig
import org.brzy.config.common.BootConfig
import org.brzy.mvc.mock.MockModConfig

class ServletTest extends JUnitSuite {
  @Test def testPath = {
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
    assertEquals(2,webapp.controllers.size)
    assertEquals(17,webapp.actions.size)

    val request = new MockHttpServletRequest("GET", "//users.brzy")
    val response = new MockHttpServletResponse()

    val context = new MockServletContext()
    context.setAttribute("application",webapp)

    val servlet = new Servlet()
    servlet.init(new MockServletConfig(context))
    servlet.service(request,response)
    assertEquals(200,response.getStatus)
  }


  @Test def testPathWithParam = {
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
    assertEquals(2,webapp.controllers.size)
    assertEquals(17,webapp.actions.size)

    val request = new MockHttpServletRequest("GET", "//users/10.brzy")
    val response = new MockHttpServletResponse()

    val context = new MockServletContext()
    context.setAttribute("application",webapp)

    val servlet = new Servlet()
    servlet.init(new MockServletConfig(context))
    servlet.service(request,response)
    assertEquals(200,response.getStatus)
  }
}