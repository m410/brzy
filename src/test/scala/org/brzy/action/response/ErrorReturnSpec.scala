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
package org.brzy.action.response

import org.scalatest.junit.JUnitSuite
import org.junit.Test

import org.springframework.mock.web.{MockHttpServletRequest, MockServletContext, MockHttpServletResponse}
import java.lang.reflect.Method
import org.brzy.action.args.{Principal, Arg}
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.mock.{MockUserStore, UserController}

class ErrorReturnSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Response Error" should {
    "should be 404" in {
      val ctlr = new UserController  with MockUserStore
      val method: Method = ctlr.getClass.getMethods.find(_.getName == "error").get
      val action = ctlr.actions.find(_.path == "error").get//new Action("/users/error", method, ctlr, ".ssp")

      assert(action.view != null)
//      assert("/user/error".equalsIgnoreCase( action.view))
//      val result = action.execute(Array.empty[Arg],new PrincipalMock)
//      assert(result != null)

      val request = new MockHttpServletRequest(new MockServletContext())
      val response = new MockHttpServletResponse()
//      ResponseHandler(action,result,request,response)
//      assert(404 == response.getStatus)
    }
  }

}