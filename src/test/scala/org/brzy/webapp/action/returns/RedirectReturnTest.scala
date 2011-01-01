package org.brzy.webapp.action.returns

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.webapp.mock.UserController
import java.lang.reflect.Method
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.Action._
import org.springframework.mock.web.{MockHttpServletRequest, MockServletContext, MockHttpServletResponse}


class RedirectReturnTest extends JUnitSuite {

  @Test def testRedirect = {
    val ctlr = new UserController()
//    val method: Method = ctlr.getClass.getMethods.find(_.getName == "redirect").get
    val action = ctlr.actions.find(_.actionPath == "redirect").get//new Action("/users", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/redirect", action.defaultView)
    val result = action.execute(List.empty[AnyRef])
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    handleResults(action, result, request, response)
    assertEquals("http://o2l.co",response.getRedirectedUrl)
  }
}