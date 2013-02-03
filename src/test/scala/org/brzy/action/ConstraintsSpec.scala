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
package org.brzy.action

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.controller.{Authorization, Controller}
import org.springframework.mock.web.MockHttpServletRequest
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class ConstraintsSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Constraints" should {
    "contain" in {
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      request.setContentType("text/xml")
      request.setSecure(true)
      val action = controller.actions.find(_.path == "index0").get
//      assertFalse(action.isConstrained(request))
    }
    "contain more" in {
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      request.setContentType("text/html")
      request.setSecure(false)
      val action = controller.actions.find(_.path == "index4").get
//      assertFalse(action.isConstrained(request))
    }
    "accept content type" in {
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      request.setContentType("text/html")
      request.setSecure(true)
      val action = controller.actions.find(_.path == "index3").get
//      assertTrue("Should be true for content type", action.isConstrained(request))
    }
    "accepty http methods" in {
      val request = new MockHttpServletRequest("POST", "//users.brzy")
      request.setContentType("text/xml")
      request.setSecure(true)
      val action = controller.actions.find(_.path == "index2").get
//      assertTrue("Should be true for http method", action.isConstrained(request))
    }
    "accept ssl" in {
      val request = new MockHttpServletRequest("GET", "//users.brzy")
      request.setContentType("text/xml")
      request.setSecure(false)
      val action = controller.actions.find(_.path == "index1").get
//      assertTrue("Should be true for secure", action.requiresSsl)
    }
  }

}