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

import args.{Principal, Parameters, ArgsBuilder}

import collection.immutable.SortedSet
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.mock.{MockUserStore, UserController}
import response.View
import org.springframework.mock.web.{MockHttpServletResponse, MockHttpServletRequest}


class ActionSpec extends WordSpec with ShouldMatchers {

  "An Action" should {
    "find action" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "").get
      val action3 = ctlr.actions.find(_.path == "create").get
      val action8 = ctlr.actions.find(_.path == "custom").get
      val action9 = ctlr.actions.find(_.path == "error").get
      val action10 = ctlr.actions.find(_.path == "json").get
      val action11 = ctlr.actions.find(_.path == "json2").get
      val action12 = ctlr.actions.find(_.path == "get").get
      val array = SortedSet(ctlr.actions:_*).toArray
      assert(17 == array.size, s"size shoul be 17, was ${array.size}")
    }
    "have default view" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "").get
      val vpath = action.view.asInstanceOf[View].path
      assert("list".equals(vpath), s"shoule be list, was $vpath" )
    }
    "parse action path" in {
      assert("/users".equals(ArgsBuilder.parseActionPath("/users.brzy","").path))
      assert("/users".equals(ArgsBuilder.parseActionPath("/users.brzy","/").path))
      assert("/users".equals(ArgsBuilder.parseActionPath("/home/users.brzy","/home").path))
      assert("/users/1/edit".equals(ArgsBuilder.parseActionPath("/users/1/edit.brzy","").path))
      assert("/path/pixel.gif".equals(ArgsBuilder.parseActionPath("/path/pixel.gif","").path))

      assert("/".equals(ArgsBuilder.parseActionPath("/.brzy","").path))
      assert("/".equals(ArgsBuilder.parseActionPath("/.brzy","/").path))
      assert("/".equals(ArgsBuilder.parseActionPath("/one/.brzy","/one").path))
      assert("/".equals(ArgsBuilder.parseActionPath("//.brzy","").path))
    }
    "parameter extract" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "{id:\\d+}/companies/{cid:\\d+}").get

      val path = "/users/1232/companies/234543"
      assert(action.isMatch("GET","",path))

      val result = action.paramsFor(path)
      assert(result != null)
      assert(2 == result.size)
    }
    "empty path" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "").get

      val path = "/users"
      assert(action.isMatch("GET","",path))

      val result = action.paramsFor(path)
      assert(result != null)
      assert(0 == result.size)
    }
    "default return path" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "save").get

      val request = new MockHttpServletRequest()
      val response = new MockHttpServletResponse()
      action.doService(request, response)
    }
    "parameter map extraction" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "{id:\\d+}/companies/{cid:\\d+}").get

      val path = "/users/1232/companies/234543"
      assert(action.isMatch("GET","",path))

      val result = action.paramsFor(path)
      assert(result != null)
      assert(2 == result.size)
    }
  }
}