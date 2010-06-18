package org.brzy.action.returns

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.springframework.mock.web.{MockHttpServletResponse, MockRequestDispatcher, MockServletContext, MockHttpServletRequest}
import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import org.brzy.action.Action
import org.junit.{Ignore, Test}
import org.brzy.mock.UserController
import org.brzy.action.ActionSupport._
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
    val action = new Action("/users/xml", ctlr.getClass.getMethods()(8), ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/xml.ssp", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertEquals("text/xml",response.getContentType)
    assertEquals("<class>UserController</class>",response.getContentAsString)
  }

}