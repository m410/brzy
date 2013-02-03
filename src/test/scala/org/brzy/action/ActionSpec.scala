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
import org.junit.Assert._
import collection.immutable.SortedSet
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.mock.{MockUserStore, UserController}


class ActionSpec extends WordSpec with ShouldMatchers {

  "An Action" should {
    "find action" in {
      val ctlr = new UserController with MockUserStore
      //    val clazz = ctlr.getClass
      //    val method = clazz.getMethods()(0)
      val action = ctlr.actions.find(_.path == "").get
      //    val action1 = ctlr.actions.find(_.path == "{id}/companies/{cid}").get
      //    val action2 = ctlr.actions.find(_.path == "save").get
      val action3 = ctlr.actions.find(_.path == "create").get
      //    val action4 = ctlr.actions.find(_.path == "{id}/delete").get
      //    val action5 = ctlr.actions.find(_.path == "{id}/update").get
      //    val action6 = ctlr.actions.find(_.path == "{id}/edit").get
      //    val action7 = ctlr.actions.find(_.path == "{id}").get
      val action8 = ctlr.actions.find(_.path == "custom").get
      val action9 = ctlr.actions.find(_.path == "error").get
      val action10 = ctlr.actions.find(_.path == "json").get
      val action11 = ctlr.actions.find(_.path == "json2").get
      val action12 = ctlr.actions.find(_.path == "other").get
      val array = SortedSet(ctlr.actions:_*).toArray
      assertEquals(16, array.size)
      assertEquals(action, array(0))  // users
      assertEquals(action3, array(1)) // users/create
      assertEquals(action8, array(2)) // users/custom
      assertEquals(action9, array(3)) // users/error
      assertEquals(action10, array(4)) // users/json
      assertEquals(action11, array(5)) // users/json2
      assertEquals(action12, array(6)) // users/other
    }
    "have default view" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "").get
      assertEquals("/user/list",action.view)
    }
    "parse action path" in {
      assertEquals("/users",ArgsBuilder.parseActionPath("/users.brzy",""))
      assertEquals("/users",ArgsBuilder.parseActionPath("/users.brzy","/"))
      assertEquals("/users",ArgsBuilder.parseActionPath("/home/users.brzy","/home"))
      assertEquals("/users/1/edit",ArgsBuilder.parseActionPath("/users/1/edit.brzy",""))

      assertEquals("/path/pixel.gif",ArgsBuilder.parseActionPath("/path/pixel.gif",""))

      assertEquals("/",ArgsBuilder.parseActionPath("/.brzy",""))
      assertEquals("/",ArgsBuilder.parseActionPath("/.brzy","/"))
      assertEquals("/",ArgsBuilder.parseActionPath("/one/.brzy","/one"))
      assertEquals("/",ArgsBuilder.parseActionPath("//.brzy",""))
    }
    "parameter extract" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "{id}/companies/{cid}").get

      val path = "/users/1232/companies/234543"
      assertTrue(action.isMatch(path,"",""))

      val result = action.paramsFor(path)
      assertNotNull(result)
      assertEquals(2, result.size)
    }
    "empty path" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "").get

      val path = "/users"
      assertTrue(action.isMatch(path,"",""))

      val result = action.paramsFor(path)
      assertNotNull(result)
      assertEquals(0, result.size)
    }
    "default return path" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "save").get

      val params = new Parameters {
        def apply(name: String) = "1"
        def get(name: String) = None
        def url = Map("id"->"1")
        def request = null
        def application = null
        def header = null
        def session = null
        def param = null
        def requestAndUrl = null
      }
      val principal = new Principal {
        def isLoggedIn = false
        def name = null
        def roles = null
      }

      action.doService(null, null)
    }
    "parameter map extraction" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "{id}/companies/{cid}").get

      val path = "/users/1232/companies/234543"
      assertTrue(action.isMatch(path,"",""))

      val result = action.paramsFor(path)
      assertNotNull(result)
      assertEquals(2, result.size)
    }
  }
}