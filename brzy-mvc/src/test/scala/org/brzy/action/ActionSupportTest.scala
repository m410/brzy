package org.brzy.action

import args.Parameters
import org.brzy.mock.UserController
import org.junit.Test
import org.junit.Assert._
import ActionSupport._
import javax.servlet.{RequestDispatcher, ServletRequest, ServletResponse}
import org.springframework.mock.web.{MockServletContext, MockRequestDispatcher, MockHttpServletRequest}
import org.scalatest.junit.JUnitSuite


class ActionSupportTest extends JUnitSuite {

  @Test
  def testBuildArgs = {
    val request = new MockHttpServletRequest("GET", "/users/10.brzy")

    val ctlr = new UserController()
    val action = new Action("/users/{id}", ctlr.getClass.getMethods()(0), ctlr, ".jsp")

    val result = buildArgs(action,request)
    assertNotNull(result)
    assertEquals(1,result.length)
    val parameters: Parameters = result(0).asInstanceOf[Parameters]
    assertEquals(1,parameters.size)
    assertEquals("10",(parameters.get("id").get)(0))
  }

  @Test
  def testParseResults = {

    val request = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/user/get.jsp",path)
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {
						assertTrue("Correct rc attribute", fwdReq.getAttribute("rc") == null)
					}
				}
			}
		}

    val tup = ("attributeKey","attributeValue")
    val ctlr = new UserController()
    val action = new Action("/users/{id}", ctlr.getClass.getMethods()(0), ctlr, ".jsp")
    handleResults(action, tup, request, null)
    assertNotNull(request.getAttribute("attributeKey"))
  }

  @Test
  def testFindActionPath = {
    val context = "/home"
    val uri = "/home/users"
    val service = new Servlet
    assertEquals("/users", findActionPath(uri,context))
  }

  @Test
  def testFindActionPath2 = {
    val context = "/home"
    val uri = "/home/user.brzy"
    val service = new Servlet
    assertEquals("/user", findActionPath(uri,context))
  }

  @Test
  def testFindActionPath3 = {
    val context = ""
    val uri = "/home/10/create.brzy"
    val service = new Servlet
    assertEquals("/home/10/create", findActionPath(uri,context))
  }

  @Test
  def testFindActionPath4 = {
    val context = "/brzy"
    val uri = "/brzy/.brzy"
    val service = new Servlet
    assertEquals("/", findActionPath(uri,context))
  }
}