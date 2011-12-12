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
package org.brzy.webapp.action.response

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web.{MockHttpServletRequest, MockServletContext, MockHttpServletResponse}
import org.brzy.webapp.mock.UserController
import java.lang.reflect.Method
import org.brzy.webapp.action.args.{Principal, Arg}

class ErrorReturnTest extends JUnitSuite {

  class PrincipalMock extends Principal {
    def isLoggedIn = false
    def name = null
    def roles = null
  }

  @Test def testError404() {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "error").get
    val action = ctlr.actions.find(_.actionPath == "error").get//new Action("/users/error", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/error", action.defaultView)
    val result = action.execute(Array.empty[Arg],new PrincipalMock)
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    ResponseHandler(action,result,request,response)
    assertEquals(404, response.getStatus)
  }
}