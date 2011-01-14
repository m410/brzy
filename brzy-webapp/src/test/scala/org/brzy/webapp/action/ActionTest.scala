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
package org.brzy.webapp.action

import args.Parameters
import org.springframework.mock.web.MockHttpServletRequest
import org.junit.Test
import org.junit.Assert._
import collection.immutable.SortedSet
import org.brzy.webapp.mock.UserController
import javax.servlet.http.HttpServletRequest
import org.easymock.EasyMock._
import org.scalatest.junit.JUnitSuite


class ActionTest extends JUnitSuite {

  @Test def testCompare = {
    val ctlr = new UserController()
//    val clazz = ctlr.getClass
//    val method = clazz.getMethods()(0)
    val action = ctlr.actions.find(_.actionPath == "").get
    val action1 = ctlr.actions.find(_.actionPath == "{id}/companies/{cid}").get //: Action = new Action("/users/{id}/companies/{cid}", method, ctlr, ".jsp")
    val action2 = ctlr.actions.find(_.actionPath == "save").get // = ctlr.actions.find(_.actionPath == "").get //: Action = new Action("/users/save", method, ctlr, ".jsp")
    val action3 = ctlr.actions.find(_.actionPath == "create").get //: Action = new Action("/users/create", method, ctlr, ".jsp")
    val action4 = ctlr.actions.find(_.actionPath == "{id}/delete").get //: Action = new Action("/users/{id}/delete", method, ctlr, ".jsp")
    val action5 = ctlr.actions.find(_.actionPath == "{id}/update").get //: Action = new Action("/users/{id}/update", method, ctlr, ".jsp")
    val action6 = ctlr.actions.find(_.actionPath == "{id}/edit").get //: Action = new Action("/users/{id}/edit", method, ctlr, ".jsp")
    val action7 = ctlr.actions.find(_.actionPath == "{id}").get //: Action = new Action("/users/{id}", method, ctlr, ".jsp")
    val action8 = ctlr.actions.find(_.actionPath == "custom").get //: Action = new Action("/users/{id}", method, ctlr, ".jsp")
    val action9 = ctlr.actions.find(_.actionPath == "error").get //: Action = new Action("/users/{id}", method, ctlr, ".jsp")
    val action10 = ctlr.actions.find(_.actionPath == "json").get //: Action = new Action("/users/{id}", method, ctlr, ".jsp")
    val action11 = ctlr.actions.find(_.actionPath == "json2").get //: Action = new Action("/users/{id}", method, ctlr, ".jsp")
    val action12 = ctlr.actions.find(_.actionPath == "other").get //: Action = new Action("/users/{id}", method, ctlr, ".jsp")
//    val actions = SortedSet(action, action1, action2, action3, action4, action5, action6, action7)
//
//    val array = new Array[Action](8)
//    actions.copyToArray(array)
    val array = SortedSet(ctlr.actions:_*).toArray
    assertEquals(16, array.size)
    assertEquals(action, array(0))  // users
    assertEquals(action3, array(1)) // users/create
    assertEquals(action8, array(2)) // users/custom
    assertEquals(action9, array(3)) // users/error
    assertEquals(action10, array(4)) // users/json
    assertEquals(action11, array(5)) // users/json2
    assertEquals(action12, array(6)) // users/other
//    assertEquals(action7, array(7)) // users/id
//    assertEquals(action1, array(8)) // users/id/companies/id
//    assertEquals(action4, array(9)) // users/id/delete
//    assertEquals(action6, array(10)) // users/id/edit
//    assertEquals(action5, array(11)) // users/id/update
//    assertEquals(action2, array(12)) // users/save

  }

  @Test def testDefaultView = {
    val ctlr = new UserController()
    val action = ctlr.actions.find(_.actionPath == "").get
    assertEquals("/user/list",action.defaultView)
  }

  @Test def testParseActionPath = {
    assertEquals("/users",Action.parseActionPath("/users.brzy",""))
    assertEquals("/users",Action.parseActionPath("/users.brzy","/"))
    assertEquals("/users",Action.parseActionPath("/home/users.brzy","/home"))
    assertEquals("/users/1/edit",Action.parseActionPath("/users/1/edit.brzy",""))
    assertEquals("/",Action.parseActionPath("/.brzy",""))
    assertEquals("/",Action.parseActionPath("/.brzy","/"))
    assertEquals("/",Action.parseActionPath("/one/.brzy","/one"))
    assertEquals("/",Action.parseActionPath("//.brzy",""))
  }

  @Test def testParameterExtract = {
    val ctlr = new UserController()
    val action = ctlr.actions.find(_.actionPath == "{id}/companies/{cid}").get

    val path = "/users/1232/companies/234543"
    assertTrue(action.path.isMatch(path))

    val result = action.path.extractParameterValues(path)
    assertNotNull(result)
    assertEquals(2, result.size)
    assertEquals("1232", result(0))
    assertEquals("234543", result(1))
  }

  @Test def testEmptyPath = {
    val ctlr = new UserController()
    val action = ctlr.actions.find(_.actionPath == "").get

    val path = "/users"
    assertTrue(action.path.isMatch(path))

    val result = action.path.extractParameterValues(path)
    assertNotNull(result)
    assertEquals(0, result.size)
  }

  @Test def testDefaultReturnPath = {
    val ctlr = new UserController()
    val action = ctlr.actions.find(_.actionPath == "save").get
    def result = action.execute(List(Parameters(Map("id"->Array("1")))))

  }

  @Test def testParameterMapExtraction = {
    val ctlr = new UserController()
    val action = ctlr.actions.find(_.actionPath == "{id}/companies/{cid}").get
    
    val path = "/users/1232/companies/234543"
    assertTrue(action.path.isMatch(path))
    
    val result = action.path.parameterNames
    assertNotNull(result)
    assertEquals(2, result.size)
    assertEquals("id", result(0))
    assertEquals("cid", result(1))
  }
}