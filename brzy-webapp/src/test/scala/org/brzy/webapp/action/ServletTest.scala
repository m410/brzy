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
package org.brzy.webapp.action

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web.{MockServletConfig, MockServletContext, MockHttpServletResponse, MockHttpServletRequest}
import org.brzy.application.{WebAppConf, WebApp}

class ServletTest extends JUnitSuite {
  @Test def testPath = {
    val webapp = WebApp(WebAppConf(env="test",defaultConfig="/brzy-webapp.test.b.yml"))
    assertNotNull(webapp)
    assertEquals(2,webapp.controllers.size)
    assertEquals(18,webapp.actions.size)

    val request = new MockHttpServletRequest("GET", "//users.brzy")
    val response = new MockHttpServletResponse()

    val context = new MockServletContext()
    context.setAttribute("application",webapp)

    val servlet = new BrzyServlet()
    servlet.init(new MockServletConfig(context))
    servlet.service(request,response)
    assertEquals(200,response.getStatus)
  }


  @Test def testPathWithParam = {
    val webapp = WebApp(WebAppConf(env="test",defaultConfig="/brzy-webapp.test.b.yml"))
    assertNotNull(webapp)
    assertEquals(2,webapp.controllers.size)
    assertEquals(18,webapp.actions.size)

    val request = new MockHttpServletRequest("GET", "//users/10.brzy")
    val response = new MockHttpServletResponse()

    val context = new MockServletContext()
    context.setAttribute("application",webapp)

    val servlet = new BrzyServlet()
    servlet.init(new MockServletConfig(context))
    servlet.service(request,response)
    assertEquals(200,response.getStatus)
  }
}