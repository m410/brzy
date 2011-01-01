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
package org.brzy.webapp.action.returns

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.springframework.mock.web.{MockHttpServletResponse, MockServletContext, MockHttpServletRequest}
//import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import org.brzy.webapp.action.Action
import org.junit.{Ignore, Test}
import org.brzy.webapp.mock.UserController
import org.brzy.webapp.action.Action._
import java.lang.reflect.Method

class XmlReturnTest  extends JUnitSuite {

  val fooXml = """<Foo>
      <bar>bar</bar>
    </Foo>"""
  val expectedXml = """<UserMock>
      <name>John</name>
    </UserMock>"""
  @Test def testXml = {
    val foo = Foo("bar")
    val xml = Xml(foo)
    assertNotNull(xml)
    assertEquals(fooXml,xml.parse)
  }


  @Test def testDefaultWithNoReturn = {
    val ctlr = new UserController()
    val action = ctlr.actions.find(_.actionPath == "xml").get

    assertNotNull(action.defaultView)
    assertEquals("/user/xml", action.defaultView)
    val result = action.execute(List[AnyRef]())
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertEquals("text/xml",response.getContentType)
    assertEquals(expectedXml,response.getContentAsString)
  }
}

case class Foo(bar:String)