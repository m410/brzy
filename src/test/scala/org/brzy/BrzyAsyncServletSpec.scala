package org.brzy

import application.{WebAppConfiguration, WebApp}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec
import org.springframework.mock.web.{MockServletConfig, MockServletContext, MockHttpServletResponse, MockHttpServletRequest}


class BrzyAsyncServletSpec extends WordSpec with ShouldMatchers with Fixtures {

  "BrzyAsyncServlet" should {
    "call async" in {
      val webapp = WebApp(WebAppConfiguration.runtime(env="test",defaultConfig="/brzy-webapp.test.b.yml"))
      assert(webapp != null)
      assert(2 == webapp.controllers.size)
      assert(19 == webapp.actions.size)

      val request = new MockHttpServletRequest("GET", "//users.brzy")
      val response = new MockHttpServletResponse()

      val context = new MockServletContext()
      context.setAttribute("application",webapp)

      val servlet = new BrzyAsyncServlet()
      servlet.init(new MockServletConfig(context))
      servlet.service(request,response)
      assert(200 == response.getStatus)
    }
  }
}
