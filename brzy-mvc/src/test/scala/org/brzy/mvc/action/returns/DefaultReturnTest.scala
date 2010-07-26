package org.brzy.mvc.action.returns

import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.mvc.action.ActionSupport._
import org.brzy.mvc.action.Action
import org.brzy.mvc.mock.UserController
import org.junit.{Ignore, Test}
import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import org.springframework.mock.web.{MockRequestDispatcher, MockHttpServletResponse, MockHttpServletRequest, MockServletContext}
import java.lang.reflect.Method

class DefaultReturnTest  extends JUnitSuite {

  @Test
  def testDefaultWithNoReturn = {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "list").get
    val action = new Action("/users", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/list", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)

    var callCount = 0
    val request = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/user/list.ssp",path)
          callCount = callCount + 1
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {}
				}
			}
		}
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertTrue(callCount == 1)
  }

  @Test def testReturnView = {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "someOther").get
    val action = new Action("/users/other", method, ctlr, ".ssp")
    assertNotNull(action.defaultView)
    assertEquals("/user/someOther", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)

    var callCount = 0
    val request = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/index.ssp",path)
          callCount = callCount + 1
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {}
				}
			}
		}
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertTrue(callCount == 1)
  }

  @Test def testReturnView2 = {
    val ctlr = new UserController()
    val method: Method = ctlr.getClass.getMethods.find(_.getName == "someOther2").get
    val action = new Action("/users/some2", method, ctlr, ".ssp")
    assertNotNull(action.defaultView)
    assertEquals("/user/someOther2", action.defaultView)
    val result = executeAction(action,Array[AnyRef]())
    assertNotNull(result)


    var callCount = 0
    val request = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/users/page.ssp",path)
          callCount = callCount + 1
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {}
				}
			}
		}
    val response = new MockHttpServletResponse()
    handleResults(action,result,request,response)
    assertTrue(callCount == 1)
  }
}