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

import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import org.springframework.mock.web.{MockHttpServletResponse, MockRequestDispatcher, MockHttpServletRequest, MockServletContext}
import javax.servlet.http.HttpServletResponse
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.mock.{MockUserStore, UserController}
import org.brzy.webapp.action.args.Arg


class DefaultReturnSpec  extends WordSpec with ShouldMatchers with Fixtures {

  "Response Return" should {

    "default with no return" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "").get

      assert(action.view != null)
      assert("list".equals( action.view.asInstanceOf[View].path))
      val result = action.execute(Array.empty[Arg],new PrincipalMock)
      assert(result != null)

      var callCount = 0
      val request = new MockHttpServletRequest(new MockServletContext()) {
        override def getRequestDispatcher(path:String):RequestDispatcher = {
          new MockRequestDispatcher(path) {
            // TODO may be expecting the wrong thing, /user/list.ssp
            assert("list.ssp".equals(path),s"expected list.ssp, but was $path")
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
      assert(callCount == 1)
    }

    "return default view" in {
      val ctlr = new UserController  with MockUserStore
      val action = ctlr.actions.find(_.path == "get").get
      assert(action.view != null)
      assert("get".equals( action.view.asInstanceOf[View].path))
      val result = action.execute(Array.empty[Arg],new PrincipalMock)
      assert(result != null)

      var callCount = 0
      val request = new MockHttpServletRequest(new MockServletContext()) {
        override def getRequestDispatcher(path:String):RequestDispatcher = {
          new MockRequestDispatcher(path) {
            assert("/index.ssp".equals(path))
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
      assert(callCount == 1)
    }

    "return default view again" in {
      val ctlr = new UserController with MockUserStore
      val action = ctlr.actions.find(_.path == "post").get
      assert(action.view != null)
      assert("post".equals( action.view.asInstanceOf[View].path))
      val result = action.execute(Array.empty[Arg],new PrincipalMock)
      assert(result != null)


      var callCount = 0
      val request = new MockHttpServletRequest(new MockServletContext()) {
        override def getRequestDispatcher(path:String):RequestDispatcher = {
          new MockRequestDispatcher(path) {
            assert("/users/page.ssp".equals(path),s"expected /users/page.ssp, but was $path")
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
      assert(callCount == 1)
    }
  }



}