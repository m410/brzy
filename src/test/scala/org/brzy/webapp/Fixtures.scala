package org.brzy.webapp

import org.springframework.mock.web.{MockHttpServletResponse, MockRequestDispatcher, MockServletContext, MockHttpServletRequest}
import javax.servlet.{FilterChain, ServletResponse, ServletRequest, RequestDispatcher}

import javax.servlet.http.HttpServletResponse


trait Fixtures {



  val request = new MockHttpServletRequest(new MockServletContext,"GET", "/companies/2") {
    override def getRequestDispatcher(path:String):RequestDispatcher = {
      new MockRequestDispatcher(path) {
        assert(false,"Should not be called")
        override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ) {
          assert(fwdReq != null)
          assert(fwdRes != null)
        }
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

  val chain = new FilterChain(){
    def doFilter(p1: ServletRequest, p2: ServletResponse) {
      assert(p1 != null)
      assert(p2 != null)
    }
  }

}
