package org.brzy

import org.springframework.mock.web.{MockHttpServletResponse, MockRequestDispatcher, MockServletContext, MockHttpServletRequest}
import javax.servlet.{FilterChain, ServletResponse, ServletRequest, RequestDispatcher}
import org.junit.Assert._
import javax.servlet.http.HttpServletResponse


trait Fixtures {

  val request1 = new MockHttpServletRequest(new MockServletContext,"GET", "/users/10") {
    override def getRequestDispatcher(path:String):RequestDispatcher = {
      new MockRequestDispatcher(path) {
        assertEquals("/users/10.brzy",path)
        override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ) {
          assertNotNull(fwdReq)
          assertNotNull(fwdRes)
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
  val response1 = new MockHttpServletResponse()

  val chain1 = new FilterChain(){
    def doFilter(p1: ServletRequest, p2: ServletResponse) {
      //        fail("Should not be called")
    }
  }


  val request = new MockHttpServletRequest(new MockServletContext,"GET", "/companies/2") {
    override def getRequestDispatcher(path:String):RequestDispatcher = {
      new MockRequestDispatcher(path) {
        fail("Should not be called")
        override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ) {
          assertNotNull(fwdReq)
          assertNotNull(fwdRes)
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
      assertNotNull(p1)
      assertNotNull(p2)
    }
  }

}
