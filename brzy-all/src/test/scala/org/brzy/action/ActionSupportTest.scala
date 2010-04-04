package org.brzy.action

import args.Parameters
import org.springframework.mock.web.MockHttpServletRequest
import org.brzy.mock.UserController
import javax.servlet.http.HttpServletRequest
import org.easymock.EasyMock._
import org.junit.Test
import org.junit.Assert._
import ActionSupport._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ActionSupportTest {

  @Test
  def testBuildArgs = {
    val request = new MockHttpServletRequest("GET", "users/10") // should be /users/

    val ctlr = new UserController()
    val action = new Action("users/{id}", ctlr.getClass.getMethods()(0), ctlr)

    val result = buildArgs(action,request)
    assertNotNull(result)
    assertEquals(1,result.length)
    assertEquals(1,result(0).asInstanceOf[Parameters].size)
  }

  @Test
  def testParseResults = {
    val clazz: Class[HttpServletRequest] = classOf[HttpServletRequest]
    val request = createMock(clazz)
    request.setAttribute("key","value")
    replay(request)

    val tup = ("key","value")

    handleResults(tup, request, null, null)

    verify(request)
  }
}