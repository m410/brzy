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
import org.springframework.mock.web.{MockFilterChain, MockFilterConfig, MockHttpServletResponse, MockHttpServletRequest}

class BrzyFilterSpec extends WordSpec with ShouldMatchers with Fixtures {

  val filter = new BrzyFilter
  filter.webapp = WebApp("test")


  "Brzy Filter" should {
    "filter Not an action" in {
      filter.doFilter(request1,response1,chain1)
    }
    "filter dispatch to" in {
      // todo need to preserve http status
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      val response = new MockHttpServletResponse()
      val chain = new MockFilterChain()
      filter.doFilter(request,response,chain)
    }
    "filter redirect to auth" in {
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      val response = new MockHttpServletResponse()
      val chain = new MockFilterChain()
      filter.doFilter(request,response,chain)
    }
    "filter redirect to secure" in {
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      val response = new MockHttpServletResponse()
      val chain = new MockFilterChain()
      filter.doFilter(request,response,chain)
    }
    "filter act on async" in {
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      val response = new MockHttpServletResponse()
      val chain = new MockFilterChain()
      filter.doFilter(request,response,chain)
    }
    "filter act on standard" in {
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      val response = new MockHttpServletResponse()
      val chain = new MockFilterChain()
      filter.doFilter(request,response,chain)
    }
  }
}