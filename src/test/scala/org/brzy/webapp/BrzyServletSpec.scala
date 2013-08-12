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
package org.brzy.webapp

import org.springframework.mock.web.{MockServletConfig, MockServletContext, MockHttpServletResponse, MockHttpServletRequest}
import org.brzy.webapp.application.{WebAppConfig, WebApp}
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec

class BrzyServletSpec extends WordSpec with ShouldMatchers {

  "Brzy Servlet" should {
    "find action by path" in {
      val webapp = WebApp(WebAppConfig.runtime(env="test",defaultConfig="/brzy-webapp.test.b.yml"))
      assert(webapp != null)
      assert(2 == webapp.controllers.size)
      assert(20 == webapp.actions.size)

      val context = new MockServletContext
      context.setAttribute("application",webapp)

      val request = new MockHttpServletRequest(context, "GET", "//users.brzy")
      val response = new MockHttpServletResponse()

      val servlet = new BrzyServlet()
      servlet.init(new MockServletConfig(context))
      servlet.service(request,response)
      assert(200 == response.getStatus)
    }

    "find path with params" in {
      val webapp = WebApp(WebAppConfig.runtime(env="test",defaultConfig="/brzy-webapp.test.b.yml"))
      assert(webapp != null)
      assert(2 == webapp.controllers.size)
      assert(20 == webapp.actions.size)

      val context = new MockServletContext
      context.setAttribute("application",webapp)

      val request = new MockHttpServletRequest(context, "GET", "//users/10.brzy")
      val response = new MockHttpServletResponse()

      val servlet = new BrzyServlet()
      servlet.init(new MockServletConfig(context))
      servlet.service(request,response)
      assert(200 == response.getStatus)
    }
  }
}