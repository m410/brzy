package org.brzy.security

import mock.SecurityMockWebApp
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web._
import org.brzy.config.webapp.WebAppConfig
import org.brzy.config.common.BootConfig
import org.brzy.config.mod.Mod
import javax.servlet.{RequestDispatcher, ServletRequest, ServletResponse}
import java.lang.String
import javax.servlet.http.HttpServletRequest
import org.brzy.application.WebApp

class SecurityFilterTest extends JUnitSuite {

  @Test def testSecured = {
    val session: MockHttpSession = new MockHttpSession
    val request = new MockHttpServletRequest("GET", "/persons/10") {
      override def getSession = session
    }
    request.setContextPath("")
    var redirectCalled = false
    val response = new MockHttpServletResponse() {
      override def sendRedirect(url: String) = {
        assertEquals("/security/authentication",url)
        redirectCalled = true
      }
    }
    val boot = new BootConfig(Map("environment" -> "development",
      "application" -> Map(
          "org" -> "org.brzy.security"
        )))
    val view = new Mod(Map("name" -> "brzy-scalate", "default_path" -> "/login"))

    val securityMod = new SecurityModConfig(Map(
      "resource_class" -> "org.brzy.security.SecurityModProvider",
      "name" -> "brzy-security",
      "default_path" -> "/security/authentication"))
    val webapp = new SecurityMockWebApp(new WebAppConfig(boot, view, Nil, List(securityMod)))
    session.getServletContext.setAttribute("application", webapp)

    val filter = new SecurityFilter
    val filterConfig: MockFilterConfig = new MockFilterConfig
    filterConfig.getServletContext.setAttribute("application",webapp)
    filter.init(filterConfig)
    filter.doFilter(request, response, new MockFilterChain)
    assertEquals("/persons/10", session.getAttribute("brzy_security_page"))
    assertTrue(redirectCalled)
  }

  @Test def testUnsecured = {
    val session: MockHttpSession = new MockHttpSession
    session.setAttribute("brzy_security_roles",Array("ROLE_USER"))
    session.setAttribute("brzy_security_login","mfortin")

    val request = new MockHttpServletRequest("GET", "/persons") {
      override def getSession = session
    }

    val response = new MockHttpServletResponse
    val boot = new BootConfig(Map("environment" -> "development",
      "application" -> Map(
          "org" -> "org.brzy.security"
        )))
    val view = new Mod(Map("name" -> "brzy-scalate", "default_path" -> "/login"))

    val securityMod = new SecurityModConfig(Map(
      "resource_class" -> "org.brzy.security.SecurityModProvider",
      "name" -> "brzy-security",
      "default_path" -> "/security/authentication"))
    val webapp = new SecurityMockWebApp(new WebAppConfig(boot, view, Nil, List(securityMod)))
    session.getServletContext.setAttribute("application", webapp)
    var doFilterCalled = false

    val chain: MockFilterChain = new MockFilterChain() {
      override def doFilter(request: ServletRequest, response: ServletResponse) = {
        doFilterCalled = true
      }
    }

    val filter = new SecurityFilter
    val filterConfig: MockFilterConfig = new MockFilterConfig
    filterConfig.getServletContext.setAttribute("application",webapp)
    filter.init(filterConfig)
    filter.doFilter(request, response, chain)
    assertTrue(doFilterCalled)
  }

  @Test def testRestricted = {
    var doSendError = false

    val session: MockHttpSession = new MockHttpSession
    session.setAttribute("brzy_security_roles",Array("ROLE_OTHER"))
    session.setAttribute("brzy_security_login","mfortin")
    val request = new MockHttpServletRequest("GET", "/persons/10") {
      override def getSession = session
    }

    val response = new MockHttpServletResponse() {
      override def sendError(status: Int, errorMessage: String) = {
        assertEquals(403,status)
        assertEquals("Restricted",errorMessage)
        doSendError = true
      }
    }
    val boot = new BootConfig(Map(
      "environment" -> "development",
      "application" -> Map(
          "org" -> "org.brzy.security"
        )
      ))
    val view = new Mod(Map("name" -> "brzy-scalate", "default_path" -> "/login"))

    val securityMod = new SecurityModConfig(Map(
      "resource_class" -> "org.brzy.security.SecurityModProvider",
      "name" -> "brzy-security",
      "default_path" -> "/security/authentication"))
    val webapp = new SecurityMockWebApp(new WebAppConfig(boot, view, Nil, List(securityMod)))
    session.getServletContext.setAttribute("application", webapp)

    val filter = new SecurityFilter
    val filterConfig: MockFilterConfig = new MockFilterConfig
    filterConfig.getServletContext.setAttribute("application",webapp)
    filter.init(filterConfig)
    filter.doFilter(request, response, new MockFilterChain)
    assertTrue(doSendError)
  }
}