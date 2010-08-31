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
package org.brzy.mvc.action.returns

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.springframework.mock.web.{MockServletContext, MockHttpServletResponse, MockHttpServletRequest}
import org.brzy.mvc.action.Action
import org.brzy.mvc.mock.UserController
import org.brzy.mvc.action.ActionSupport._
import java.lang.reflect.Method

class JsonReturnTest  extends JUnitSuite {

  @Test def testReturnJson = {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "json").get
    val action = new Action("/users/json", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/json", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertEquals("text/json",response.getContentType)
    assertEquals("{}",response.getContentAsString)
  }

  @Test def testReturnJson2 = {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "json2").get
    val action = new Action("/users/json2", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/json2", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertEquals("text/json",response.getContentType)
    assertEquals("{\"name\":\"value\"}",response.getContentAsString)
  }
}