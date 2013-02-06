package org.brzy.action.response

import org.junit.Test

import org.scalatest.junit.JUnitSuite
import org.springframework.mock.web.{MockHttpServletRequest, MockServletContext, MockHttpServletResponse}
import org.brzy.action.args.{Arg, Principal}
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.mock.{MockUserStore, UserController}


class RedirectReturnSpec extends WordSpec with ShouldMatchers {

  "Response Redirect" should {
    "be returned by action" in {
      val ctlr = new UserController with MockUserStore
      //    val method: Method = ctlr.getClass.getMethods.find(_.getName == "redirect").get
      val action = ctlr.actions.find(_.path == "redirect").get//new Action("/users", method, ctlr, ".ssp")

      assert(action.view != null)
//      assert("/user/redirect".equalsIgnoreCase( action.view))
//      val result = action.execute(Array.empty[Arg],new PrincipalMock)
//      assert(result != null)

//      val request = new MockHttpServletRequest(new MockServletContext())
//      val response = new MockHttpServletResponse()
//      ResponseHandler(action, result, request, response)
//      assertEquals("http://o2l.co",response.getRedirectedUrl)
    }
  }
}