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

import Action._

import args.{ArgsBuilder, Parameters}

import org.junit.Test
import org.junit.Assert._

import javax.servlet.{RequestDispatcher, ServletRequest, ServletResponse}
import org.springframework.mock.web.{MockServletContext, MockRequestDispatcher, MockHttpServletRequest, MockHttpServletResponse}
import org.scalatest.junit.JUnitSuite
import response.ResponseHandler
import javax.servlet.http.HttpServletResponse
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.BrzyServlet


class ActionCompanionSpec extends WordSpec with ShouldMatchers with Fixtures {

  "ArgsBuilder" should {

    "make args for action" in {
      val request = new MockHttpServletRequest("GET", "/users/10.brzy")

      val ctlr = new UserController()
      val action = ctlr.actions.find(_.path == "{id}").get//new Action("/users/{id}", ctlr.getClass.getMethods()(0), ctlr, ".jsp")

      val result = ArgsBuilder(request, action)
      assertNotNull(result)
      assertEquals(1,result.length)
      val parameters: Parameters = result(0).asInstanceOf[Parameters]
      assertEquals(1,parameters.url.size)
      assertEquals("10",parameters("id"))
    }

    "parse results" in {
      val tup = ("attributeKey","attributeValue")
      val ctlr = new UserController()
      val action = ctlr.actions.find(_.path == "{id}").get//new Action("/users/{id}", ctlr.getClass.getMethods()(0), ctlr, ".jsp")
      ResponseHandler(action, tup, request, response)
      assertNotNull(request.getAttribute("attributeKey"))
    }
    "find action path" in {
      val context = "/home"
      val uri = "/home/users"
      val service = new BrzyServlet
      assertEquals("/users", ArgsBuilder.parseActionPath(uri,context))
    }
    "find action path 2" in {
      val context = "/home"
      val uri = "/home/user.brzy"
      val service = new BrzyServlet
      assertEquals("/user", ArgsBuilder.parseActionPath(uri,context))
    }
    "find action path 2" in {
      val context = ""
      val uri = "/home/10/create.brzy"
      val service = new BrzyServlet
      assertEquals("/home/10/create", ArgsBuilder.parseActionPath(uri,context))
    }
    "find action path 3" in {
      val context = "/brzy"
      val uri = "/brzy/.brzy"
      val service = new BrzyServlet
      assertEquals("/", ArgsBuilder.parseActionPath(uri,context))
    }
  }


}