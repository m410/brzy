package org.brzy.action.returns

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.springframework.mock.web.{MockServletContext, MockHttpServletResponse, MockHttpServletRequest}
import org.brzy.action.Action
import org.brzy.mock.UserController
import org.brzy.action.ActionSupport._
import java.lang.reflect.Method

class JsonReturnTest  extends JUnitSuite {

  @Test def testReturnJson = {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "json").get
    val action = new Action("/users/json", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/json.ssp", action.defaultView)
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
    assertEquals("/user/json2.ssp", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertEquals("text/json",response.getContentType)
    assertEquals("{\"name\":\"value\"}",response.getContentAsString)
  }
}