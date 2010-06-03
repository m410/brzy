package org.brzy.action

import org.junit.Test
import org.junit.Assert._
import javax.servlet.{RequestDispatcher, ServletResponse, ServletRequest, FilterChain}
import org.springframework.mock.web.{MockRequestDispatcher, MockServletContext, MockHttpServletRequest, MockHttpServletResponse}
import org.scalatest.junit.JUnitSuite

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class FilterTest extends JUnitSuite {


  @Test
  def testFilterForward = {
    val request = new MockHttpServletRequest(new MockServletContext,"GET", "/users/10") {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/users/10.brzy",path)
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {
						assertNotNull(fwdReq)
						assertNotNull(fwdRes)
					}
				}
			}
		}
    val response = new MockHttpServletResponse()

    val chain = new FilterChain(){
      def doFilter(p1: ServletRequest, p2: ServletResponse) = {
        fail("Should not be called")
      }
    }

    val filter = new Filter
    filter.doFilter(request,response,chain)
  }

  @Test
  def testFilterPass = {
    val request = new MockHttpServletRequest(new MockServletContext,"GET", "/users/get.jsp") {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          fail("Should not be called")
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {
						assertNotNull(fwdReq)
						assertNotNull(fwdRes)
					}
				}
			}
		}
    val response = new MockHttpServletResponse()

    val chain = new FilterChain(){
      def doFilter(p1: ServletRequest, p2: ServletResponse) = {
        assertNotNull(p1)
        assertNotNull(p2)
      }
    }

    val filter = new Filter
    filter.doFilter(request,response,chain)
  }
}