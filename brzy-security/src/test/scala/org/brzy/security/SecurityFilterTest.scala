/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.security

import mock.SecurityMockWebApp
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web._
import javax.servlet.{ServletRequest, ServletResponse}
import java.lang.String
import org.brzy.application.WebAppConf

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

    val webapp = new SecurityMockWebApp(WebAppConf("test"))
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
    val webapp = new SecurityMockWebApp(WebAppConf("test"))
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
    val webapp = new SecurityMockWebApp(WebAppConf("test"))
    session.getServletContext.setAttribute("application", webapp)

    val filter = new SecurityFilter
    val filterConfig: MockFilterConfig = new MockFilterConfig
    filterConfig.getServletContext.setAttribute("application",webapp)
    filter.init(filterConfig)
    filter.doFilter(request, response, new MockFilterChain)
    assertTrue(doSendError)
  }
}