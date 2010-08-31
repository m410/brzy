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

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.springframework.mock.web.{MockHttpServletResponse, MockRequestDispatcher, MockServletContext, MockHttpServletRequest}
import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import org.brzy.mvc.action.Action
import org.junit.{Ignore, Test}
import org.brzy.mvc.mock.UserController
import org.brzy.mvc.action.ActionSupport._
import java.lang.reflect.Method

class XmlReturnTest  extends JUnitSuite {

  @Test @Ignore def listMethods = {
    val ctlr = new UserController()
    var count = 0
    ctlr.getClass.getMethods.foreach(f=>{
      println("method["+count+"] - "+f.getName)
      count = count + 1
    })
  }

  @Test def testDefaultWithNoReturn = {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "xml").get
    val action = new Action("/users/xml", ctlr.getClass.getMethods.find(_.getName == "xml").get, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/xml", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertEquals("text/xml",response.getContentType)
    assertEquals("<class>UserController</class>",response.getContentAsString)
  }

}