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
package org.brzy.webapp.application

import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.springframework.mock.web.MockHttpServletRequest
import org.brzy.webapp._


class WebAppSpec extends WordSpec with ShouldMatchers  with Fixture {

  val webapp = WebApp("test")

  "WebApp" should {
    "create webapp" in {
      val webappConf = WebAppConfiguration.runtime(env="test",defaultConfig="/brzy-webapp.test.b.yml")
      assert(webappConf != null)

      val webapp = WebApp(webappConf)
      assert(webapp != null)

      assert(webapp.services != null)
      assert(1 == webapp.services.size, s"expected 1, was ${webapp.services.size}")

      assert(webapp.controllers != null)
      assert(2 == webapp.controllers.size, s"expected 2, was ${webapp.controllers.size}")

      assert(webapp.actions != null)
      assert(20 == webapp.actions.size, s"expected 20, was ${webapp.actions.size}")
    }
    "call doFilter and return dispath to servlet" in {
      val request = new MockHttpServletRequest("GET", "/users_none/pass_through.gif")
      val action = webapp.doFilterAction(request)
      action match {
        case NotAnAction =>
        case _ => assert(false, s"expected NotAnAction, was $action")
      }
    }
    "call doFilter and return dispath to .brzy" in {
      val request = new MockHttpServletRequest("GET", "/users")
      val action = webapp.doFilterAction(request)
      assert(action.isInstanceOf[DispatchTo], s"expected DispatchTo, was $action")
    }
    "call doFilter and return dispath to async servlet" in {
      val request = new MockHttpServletRequest("GET", "/userArgs/.brzy")
      val action = webapp.doFilterAction(request)
      assert(action.isInstanceOf[RedirectToAuthenticate], s"expected RedirectToAuthenticate, was $action")
    }
    "call doFilter and return redirect to ssl" in {
      val request = new MockHttpServletRequest("GET", "/userArgs/123")
      val action = webapp.doFilterAction(request)
      assert(action.isInstanceOf[RedirectToSecure], s"expected RedirectToSecure, was $action")
    }
    "call doFilter and return async redirect" in {
      val request = new MockHttpServletRequest("GET", "/users/async")
      val action = webapp.doFilterAction(request)
      assert(action.isInstanceOf[DispatchTo], s"expected DispathTo, was $action")
    }
    "call doFilter and return async action" in {
      val request = new MockHttpServletRequest("GET", "/users/async.brzy_async")
      val action = webapp.doFilterAction(request)
      assert(action.isInstanceOf[ActOnAsync], s"expected ActOnAsync, was $action")
    }
    "call doFilter and return action" in {
      val request = new MockHttpServletRequest("GET", "/users.brzy")
      val action = webapp.doFilterAction(request)
      assert(action.isInstanceOf[ActOn], s"expected ActOn, was $action")
    }
  }
}