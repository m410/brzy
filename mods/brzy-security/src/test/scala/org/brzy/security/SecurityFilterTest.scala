package org.brzy.security

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web._
import org.brzy.mock.MockWebApp
import org.brzy.config.webapp.WebAppConfig
import org.brzy.config.common.BootConfig
import org.brzy.config.mod.Mod

class SecurityFilterTest extends JUnitSuite {

  @Test def testSecured = {
    val session: MockHttpSession = new MockHttpSession
    val request = new MockHttpServletRequest("GET", "/persons/10") {
      override def getSession = session
    }
    request.setContextPath("")
    val response = new MockHttpServletResponse
    val chain = new MockFilterChain

    val boot = new BootConfig(Map("environment" -> "development"))
    val view = new Mod(Map("name"->"brzy-scalate","default_path"->"/login"))

    val securityMod = new SecurityModConfig(Map(
      "resource_class"->"org.brzy.security.SecurityModResource",
      "name"->"brzy-security",
      "default_path"->"/login"))
    val webapp = new MockWebApp(new WebAppConfig(boot,view,Nil,List(securityMod)))
    session.getServletContext.setAttribute("application",webapp)

    def filter = new SecurityFilter
    filter.init(new MockFilterConfig)
    filter.doFilter(request, response, chain)
    assertEquals("/persons/10",session.getAttribute("brzy_security_page"))
//    assertEquals("",mockHttpSession.getAttribute("brzy_security_login"))
  }

  @Test def testUnsecured = {

  }

  @Test def testRestricted = {

  }
}