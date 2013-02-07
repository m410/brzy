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

import org.brzy.BrzyServlet
import args.{ArgsBuilder, Parameters}



import org.springframework.mock.web.{MockServletContext, MockRequestDispatcher, MockHttpServletRequest, MockHttpServletResponse}

import response.ResponseHandler
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers


class ActionCompanionSpec extends WordSpec with ShouldMatchers with Fixtures {

  "ArgsBuilder" should {

    "make args for action" in {
      val request = new MockHttpServletRequest("GET", "/users/10/items/123.brzy")

      val ctlr = new UserController()
      val action = ctlr.actions.find(_.path == "{id}/items/{iid}").get

      val result = ArgsBuilder(request, action)
      assert(result != null)
      assert(1 == result.length)
      val parameters: Parameters = result(0).asInstanceOf[Parameters]
      assert(2 == parameters.url.size)
      assert("10".equalsIgnoreCase(parameters("id")))
    }

    "parse results" in {
      val tup = ("attributeKey","attributeValue")
      val ctlr = new UserController()
      val action = ctlr.actions.find(_.path == "{id}/items/{iid}").get
      ResponseHandler(action, tup, request, response)
      assert(null != request.getAttribute("attributeKey"))
    }
    "find action path" in {
      val context = "/home"
      val uri = "/home/users"
      val service = new BrzyServlet
      val actionPath = ArgsBuilder.parseActionPath(uri, context)
      assert("/users".equalsIgnoreCase(actionPath.path))
      assert(!actionPath.isServlet)
    }
    "find action path 2" in {
      val context = "/home"
      val uri = "/home/user.brzy"
      val service = new BrzyServlet
      val actionPath = ArgsBuilder.parseActionPath(uri, context)
      assert("/user".equalsIgnoreCase(actionPath.path), s"should be '/user', but was ${actionPath.path}")
      assert(actionPath.isServlet)
    }
    "find action path 3" in {
      val context = ""
      val uri = "/home/10/create.brzy"
      val service = new BrzyServlet
      val actionPath = ArgsBuilder.parseActionPath(uri, context)
      assert("/home/10/create".equalsIgnoreCase(actionPath.path), s"should be '/home/10/create', but was ${actionPath.path}")
      assert(actionPath.isServlet)
    }
    "find action path 4" in {
      val context = "/brzy"
      val uri = "/brzy/.brzy"
      val service = new BrzyServlet
      val actionPath = ArgsBuilder.parseActionPath(uri, context)
      assert("/".equalsIgnoreCase(actionPath.path), s"should be '/', but was ${actionPath.path}")
      assert(actionPath.isServlet)
    }
  }


}