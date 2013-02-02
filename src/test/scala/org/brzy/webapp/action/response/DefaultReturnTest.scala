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
package org.brzy.webapp.action.response

import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.webapp.mock.UserController
import org.junit.Test
import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import org.springframework.mock.web.{MockRequestDispatcher, MockHttpServletResponse, MockHttpServletRequest, MockServletContext}
import org.brzy.webapp.action.args.{Principal, Arg}
import javax.servlet.http.HttpServletResponse

class DefaultReturnTest  extends JUnitSuite {

  class PrincipalMock extends Principal {
    def isLoggedIn = false
    def name = null
    def roles = null
  }

  @Test
  def testDefaultWithNoReturn() {
    val ctlr = new UserController()
//    val method: Method = ctlr.getClass.getMethods.find(_.getName == "list").get
    val action = ctlr.actions.find(_.actionPath == "").get//new Action("/users", method, ctlr, ".ssp")

    assertNotNull(action.defaultView)
    assertEquals("/user/list", action.defaultView)
    val result = action.execute(Array.empty[Arg],new PrincipalMock)
    assertNotNull(result)

    var callCount = 0
    val request = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/user/list.ssp",path)
          callCount = callCount + 1
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ){}
				}
			}
      def startAsync() = null
      def startAsync(p1: ServletRequest, p2: ServletResponse) = null
      def isAsyncStarted = false
      def isAsyncSupported = false
      def getAsyncContext = null
      def getDispatcherType = null

      def authenticate(p1: HttpServletResponse) = false
      def login(p1: String, p2: String) {}
      def logout() {}
      def getParts = null
      def getPart(p1: String) = null
		}
    val response = new MockHttpServletResponse()
    ResponseHandler(action,result,request,response)
    assertTrue(callCount == 1)
  }

  @Test def testReturnView() {
    val ctlr = new UserController()
//    val method: Method = ctlr.getClass.getMethods.find(_.getName == "someOther").get
    val action = ctlr.actions.find(_.actionPath == "other").get//new Action("/users/other", method, ctlr, ".ssp")
    assertNotNull(action.defaultView)
    assertEquals("/user/other", action.defaultView)
    val result = action.execute(Array.empty[Arg],new PrincipalMock)
    assertNotNull(result)

    var callCount = 0
    val request = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/index.ssp",path)
          callCount = callCount + 1
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ){}
				}
			}
      def startAsync() = null
      def startAsync(p1: ServletRequest, p2: ServletResponse) = null
      def isAsyncStarted = false
      def isAsyncSupported = false
      def getAsyncContext = null
      def getDispatcherType = null

      def authenticate(p1: HttpServletResponse) = false
      def login(p1: String, p2: String) {}
      def logout() {}
      def getParts = null
      def getPart(p1: String) = null
		}
    val response = new MockHttpServletResponse()
    ResponseHandler(action,result,request,response)
    assertTrue(callCount == 1)
  }

  @Test def testReturnView2() {
    val ctlr = new UserController()
//    val method: Method = ctlr.getClass.getMethods.find(_.getName == "someOther2").get
    val action = ctlr.actions.find(_.actionPath == "other2").get//new Action("/users/some2", method, ctlr, ".ssp")
    assertNotNull(action.defaultView)
    assertEquals("/user/other2", action.defaultView)
    val result = action.execute(Array.empty[Arg],new PrincipalMock)
    assertNotNull(result)


    var callCount = 0
    val request = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/users/page.ssp",path)
          callCount = callCount + 1
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ) {}
				}
			}
      def startAsync() = null
      def startAsync(p1: ServletRequest, p2: ServletResponse) = null
      def isAsyncStarted = false
      def isAsyncSupported = false
      def getAsyncContext = null
      def getDispatcherType = null

      def authenticate(p1: HttpServletResponse) = false
      def login(p1: String, p2: String) {}
      def logout() {}
      def getParts = null
      def getPart(p1: String) = null
		}
    val response = new MockHttpServletResponse()
    ResponseHandler(action,result,request,response)
    assertTrue(callCount == 1)
  }
}