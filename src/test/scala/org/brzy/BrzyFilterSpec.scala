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
package org.brzy


import org.brzy.application.WebApp
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec
import org.springframework.mock.web.{MockFilterChain, MockHttpServletResponse, MockHttpServletRequest}
import javax.servlet.{ServletResponse, ServletRequest, FilterChain}

class BrzyFilterSpec extends WordSpec with ShouldMatchers with Fixtures {

  val filter = new BrzyFilter
  filter.webapp = WebApp("test")


  "Brzy Filter" should {
    "filter Not an action" in {
      filter.doFilter(request1,response1,chain1)
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