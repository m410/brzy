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

import org.brzy.webapp.application.WebApp
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec
import org.springframework.mock.web._
import javax.servlet.{RequestDispatcher, ServletResponse, ServletRequest, FilterChain}
import javax.servlet.http.HttpServletResponse

class BrzyFilterSpec extends WordSpec with ShouldMatchers with Fixtures {

  val filter = new BrzyFilter
  filter.webapp = WebApp("test")


  "Brzy Filter" should {
    "filter Not an action" in {
      val request1 = new MockHttpServletRequest(new MockServletContext,"GET", "/users/10") {
        override def getRequestDispatcher(path:String):RequestDispatcher = {
          new MockRequestDispatcher(path) {
            assert("/users/10.brzy".equalsIgnoreCase(path),s"expected '/users/10.brzy', was '$path'")
            override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ) {
              assert(fwdReq != null)
              assert(fwdRes != null)
            }
          }
        }

        def startAsync() = null
        def startAsync(p1: ServletRequest, p2: ServletResponse) = null
        def isAsyncStarted = false
        def isAsyncSupported = false
        def getAsyncContext = null
        def getDispatcherType = null

        def authenticate(p1: HttpServletResponse) = false
        def login(p1: String, p2: String) {}
        def logout() {}
        def getParts = null
        def getPart(p1: String) = null
      }
      val response1 = new MockHttpServletResponse()

      val chain1 = new FilterChain(){
        def doFilter(p1: ServletRequest, p2: ServletResponse) {
          //        fail("Should not be called")
        }
      }
      filter.doFilter(request1, response1, chain1)

    }
    "filter dispatch pass through" in {
      // todo need to preserve http status
      val request = new MockHttpServletRequest("GET", "/users/pass_through.gif")
      val response = new MockHttpServletResponse()
      var called = false
      val chain = new FilterChain(){
        def doFilter(p1: ServletRequest, p2: ServletResponse) {
          called = true
        }
      }
      filter.doFilter(request,response,chain)
      assert(called)
    }
    "filter redirect to auth" in {
      val request = new MockHttpServletRequest("GET", "/userArgs/.brzy")
      val response = new MockHttpServletResponse()
      var called = false
      val chain = new FilterChain(){
        def doFilter(p1: ServletRequest, p2: ServletResponse) {
          called = true
        }
      }
      filter.doFilter(request,response,chain)
      assert(!called)
    }
    "filter redirect to secure" in {
      val request = new MockHttpServletRequest("GET", "/userArgs/123.brzy")
      val response = new MockHttpServletResponse()
      var called = false
      val chain = new FilterChain(){
        def doFilter(p1: ServletRequest, p2: ServletResponse) {
          called = true
        }
      }
      filter.doFilter(request,response,chain)
      assert(!called)
    }
    "filter act on async" in {
      val request = new MockHttpServletRequest("GET", "/users/async.brzy_async")
      val response = new MockHttpServletResponse()
      var called = false
      val chain = new FilterChain(){
        def doFilter(p1: ServletRequest, p2: ServletResponse) {
          called = true
        }
      }
      filter.doFilter(request,response,chain)
      assert(called)
    }
    "filter act on standard" in {
      val request = new MockHttpServletRequest("GET", "/users.brzy")
      val response = new MockHttpServletResponse()
      var called = false
      val chain = new FilterChain(){
        def doFilter(p1: ServletRequest, p2: ServletResponse) {
          called = true
        }
      }
      filter.doFilter(request,response,chain)
      assert(called)
    }
  }
}