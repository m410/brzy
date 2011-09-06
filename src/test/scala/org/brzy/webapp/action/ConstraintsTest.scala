package org.brzy.webapp.action

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
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.webapp.controller.{Secured, Controller}
import org.springframework.mock.web.MockHttpServletRequest

class ConstraintsTest extends JUnitSuite {

  val controller = new Controller("") with Secured {
		override val constraints = List(Roles("ADMIN"))
		def actions = List(
      Action("index","index",index _,Secure(),HttpMethods(HttpMethod.GET),ContentTypes("text/xml")),
      Action("index","index",index _,Secure()),
      Action("index","index",index _,HttpMethods(HttpMethod.GET)),
      Action("index","index",index _,ContentTypes("text/xml")),
      Action("index2","index2",index2 _)
    )
		def index = "name" -> "value"
		def index2 = "name" -> "value"
	}

  @Test def passConstraintTest() {
    val request = new MockHttpServletRequest("GET", "//users.brzy")
    request.setContentType("text/xml")
    request.setSecure(true)
    val action = controller.actions(0)
    assertFalse(action.isConstrained(request))
  }

  @Test def contentTypeConstraintTest() {
    val request = new MockHttpServletRequest("GET", "//users.brzy")
    request.setContentType("text/html")
    request.setSecure(true)
    val action = controller.actions(3)
    assertTrue("Should be true for content type", action.isConstrained(request))
  }

  @Test def httpMethodConstraintTest() {
    val request = new MockHttpServletRequest("POST", "//users.brzy")
    request.setContentType("text/xml")
    request.setSecure(true)
    val action = controller.actions(2)
    assertTrue("Should be true for http method", action.isConstrained(request))
  }

  @Test def secureConstraintTest() {
    val request = new MockHttpServletRequest("GET", "//users.brzy")
    request.setContentType("text/xml")
    request.setSecure(false)
    val action = controller.actions(1)
    assertFalse("Should be true for secure", action.isConstrained(request))
  }
}