package org.brzy.webapp

import application.{WebAppConfig, WebApp}

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec
import org.springframework.mock.web.{MockServletConfig, MockServletContext, MockHttpServletResponse, MockHttpServletRequest}
import javax.servlet.http.HttpServletResponse
import javax.servlet._


class BrzyAsyncServletSpec extends WordSpec with ShouldMatchers with Fixtures {

  "BrzyAsyncServlet" should {
    "call async" ignore {
      val webapp = WebApp(WebAppConfig.runtime(env="test",defaultConfig="/brzy-webapp.test.b.yml"))
      assert(webapp != null)
      assert(2 == webapp.controllers.size)
      assert(20 == webapp.actions.size)

      var callCount = 0

      val context = new MockServletContext()
      context.setAttribute("application",webapp)

      val request = new MockHttpServletRequest(context, "GET", "/users/async.brzy") {
        def startAsync() = new AsyncContext {
          def dispatch(p1: ServletContext, p2: String) {}
          def dispatch(p1: String) {}
          def dispatch() {}
          def getRequest = null
          def getTimeout = 0
          def createListener[T <: AsyncListener](p1: Class[T]) = null.asInstanceOf[T]
          def setTimeout(p1: Long) {}
          def addListener(p1: AsyncListener, p2: ServletRequest, p3: ServletResponse) {}
          def addListener(p1: AsyncListener) {}
          def complete() {}
          def hasOriginalRequestAndResponse = false
          def getResponse = null
          def start(p1: Runnable) { callCount = callCount + 1 }
        }
        def startAsync(p1: ServletRequest, p2: ServletResponse) =new AsyncContext {
          def dispatch(p1: ServletContext, p2: String) {}
          def dispatch(p1: String) {}
          def dispatch() {}
          def getRequest = p1
          def getTimeout = 0
          def createListener[T <: AsyncListener](p1: Class[T]) = null.asInstanceOf[T]
          def setTimeout(p1: Long) {}
          def addListener(p1: AsyncListener, p2: ServletRequest, p3: ServletResponse) {}
          def addListener(p1: AsyncListener) {}
          def complete() {}
          def hasOriginalRequestAndResponse = false
          def getResponse = p2
          def start(p1: Runnable) { callCount = callCount + 1 }
        }
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



      val servlet = new BrzyAsyncServlet()
      servlet.init(new MockServletConfig(context))
      servlet.service(request,response)
      assert(200 == response.getStatus)
      assert(callCount == 1)
    }
  }
}
