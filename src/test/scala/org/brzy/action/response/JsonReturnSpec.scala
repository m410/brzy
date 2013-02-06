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


import java.lang.reflect.Method
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.mock.{MockUserStore, UserController}

class JsonReturnSpec extends WordSpec with ShouldMatchers {

  val expected = """{"id":1,"version":0,"name":"hello"}"""

  "Response Json" should {

    "return json text" in {
      val ctlr = new UserController  with MockUserStore
      //    val method: Method = ctlr.getClass.getMethods.find(_.getName == "json").get
      val action = ctlr.actions.find(_.path == "json").get//new Action("/users/json", method, ctlr, ".ssp")

      assert(action.view != null)
//      assert("/user/json".equalsIgnoreCase( action.view))
//      val result = action.execute(Array.empty[Arg],new PrincipalMock)
//      assert(result != null)

//      val request = new MockHttpServletRequest(new MockServletContext())
//      val response = new MockHttpServletResponse()
//      ResponseHandler(action,result,request,response)
//      assertEquals("application/json",response.getContentType)
//      assertEquals(expected,response.getContentAsString)
    }

    "return more json" in {
      val ctlr = new UserController  with MockUserStore
      val method: Method = ctlr.getClass.getMethods.find(_.getName == "json2").get
      val action = ctlr.actions.find(_.path == "json2").get//new Action("/users/json2", method, ctlr, ".ssp")

      assert(action.view != null)
//      assert("/user/json2".equalsIgnoreCase( action.view))
//      val result = action.execute(Array.empty[Arg],new PrincipalMock)
//      assert(result != null)

//      val request = new MockHttpServletRequest(new MockServletContext())
//      val response = new MockHttpServletResponse()
//      ResponseHandler(action,result,request,response)
//      assertEquals("application/json",response.getContentType)
//      assertEquals("{\"name\":\"value\"}",response.getContentAsString)
    }
  }
}