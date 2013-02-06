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

import org.springframework.mock.web.{MockHttpServletResponse, MockServletContext, MockHttpServletRequest}

import org.junit.Test
import org.scalatest.junit.JUnitSuite


import org.brzy.action.args.{Principal, Arg}
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.mock.{MockUserStore, UserController}

class XmlReturnSpec  extends WordSpec with ShouldMatchers with Fixtures {

  val fooXml = """<Foo>
      <bar>bar</bar>
    </Foo>"""

  val expectedXml = """<UserMock>
      <name>John</name>
    </UserMock>"""

  "Response Xml" should {
    "be returned by action" in {
      val foo = Foo("bar")
      val xml = Xml(foo)
      assert(xml != null)
      assert(fooXml.equalsIgnoreCase(xml.parse))
    }
    "accept xml" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "xml").get

      assert(action.view != null)
//      assert("/user/xml".equals(action.view))
//      val result = action.execute(Array.empty[Arg],new PrincipalMock)
//      assert(result != null)

//      val request = new MockHttpServletRequest(new MockServletContext())
//      val response = new MockHttpServletResponse()
//      ResponseHandler(action,result,request,response)
//      assertEquals("text/xml",response.getContentType)
//      assertEquals(expectedXml,response.getContentAsString)
    }
  }
}

