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
      Action("index0", "index0", index _ ,Ssl(),HttpMethods(HttpMethod.GET),ContentTypes("text/xml")),
      Action("index1", "index1", index _ ,Ssl()),
      Action("index2", "index2", index _ ,HttpMethods(HttpMethod.GET)),
      Action("index3", "index3", index _ ,ContentTypes("text/xml")),
      Action("index4", "index4", index2 _)
    )
		def index = "name" -> "value"
		def index2 = "name" -> "value"
	}

  @Test def passConstraintTest() {
    val request = new MockHttpServletRequest("GET", "//users.brzy")
    request.setContentType("text/xml")
    request.setSecure(true)
    val action = controller.actions.find(_.actionPath == "index0").get
    assertFalse(action.isConstrained(request))
  }

  @Test def passConstraintTest2() {
    val request = new MockHttpServletRequest("GET", "//users.brzy")
    request.setContentType("text/html")
    request.setSecure(false)
    val action = controller.actions.find(_.actionPath == "index4").get
    assertFalse(action.isConstrained(request))
  }

  @Test def contentTypeConstraintTest() {
    val request = new MockHttpServletRequest("GET", "//users.brzy")
    request.setContentType("text/html")
    request.setSecure(true)
    val action = controller.actions.find(_.actionPath == "index3").get
    assertTrue("Should be true for content type", action.isConstrained(request))
  }

  @Test def httpMethodConstraintTest() {
    val request = new MockHttpServletRequest("POST", "//users.brzy")
    request.setContentType("text/xml")
    request.setSecure(true)
    val action = controller.actions.find(_.actionPath == "index2").get
    assertTrue("Should be true for http method", action.isConstrained(request))
  }

  @Test def requireSslConstraintTest() {
    val request = new MockHttpServletRequest("GET", "//users.brzy")
    request.setContentType("text/xml")
    request.setSecure(false)
    val action = controller.actions.find(_.actionPath == "index1").get
    assertTrue("Should be true for secure", action.requiresSsl)
  }
}