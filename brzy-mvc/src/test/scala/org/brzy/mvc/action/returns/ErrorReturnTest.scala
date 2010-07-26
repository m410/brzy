package org.brzy.mvc.action.returns

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web.{MockHttpServletRequest, MockServletContext, MockHttpServletResponse}
import org.brzy.mvc.mock.UserController
import org.brzy.mvc.action.Action
import org.brzy.mvc.action.ActionSupport._
import java.lang.reflect.Method

class ErrorReturnTest extends JUnitSuite {

  @Test def testError404 = {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "error").get
    val action = new Action("/users/error", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/error", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertEquals(404, response.getStatus)
  }
}