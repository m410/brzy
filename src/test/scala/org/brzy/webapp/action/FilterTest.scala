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
package org.brzy.webapp.action

import org.junit.Test
import org.junit.Assert._
import javax.servlet.{RequestDispatcher, ServletResponse, ServletRequest, FilterChain}
import org.springframework.mock.web.{MockRequestDispatcher, MockServletContext, MockHttpServletRequest, MockHttpServletResponse}
import org.scalatest.junit.JUnitSuite


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

    val filter = new BrzyFilter
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

    val filter = new BrzyFilter
    filter.doFilter(request,response,chain)
  }
}