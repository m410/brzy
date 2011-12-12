package org.brzy.webapp.action.response

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.webapp.mock.UserController
import org.springframework.mock.web.{MockHttpServletRequest, MockServletContext, MockHttpServletResponse}
import org.brzy.webapp.action.args.{Arg, Principal}


class RedirectReturnTest extends JUnitSuite {

  class PrincipalMock extends Principal {
    def isLoggedIn = false
    def name = null
    def roles = null
  }

  @Test def testRedirect() {
    val ctlr = new UserController()
//    val method: Method = ctlr.getClass.getMethods.find(_.getName == "redirect").get
    val action = ctlr.actions.find(_.actionPath == "redirect").get//new Action("/users", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/redirect", action.defaultView)
    val result = action.execute(Array.empty[Arg],new PrincipalMock)
    assertNotNull(result)

    val request = new MockHttpServletRequest(new MockServletContext())
    val response = new MockHttpServletResponse()
    ResponseHandler(action, result, request, response)
    assertEquals("http://o2l.co",response.getRedirectedUrl)
  }
}