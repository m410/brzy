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

import args.Parameters
import org.brzy.webapp.mock.UserController
import Action._
import org.brzy.webapp.BrzyServlet

import org.junit.Test
import org.junit.Assert._

import javax.servlet.{RequestDispatcher, ServletRequest, ServletResponse}
import org.springframework.mock.web.{MockServletContext, MockRequestDispatcher, MockHttpServletRequest}
import org.scalatest.junit.JUnitSuite


class ActionCompanionTest extends JUnitSuite {

  @Test def testBuildArgs = {
    val request = new MockHttpServletRequest("GET", "/users/10.brzy")

    val ctlr = new UserController()
    val action = ctlr.actions.find(_.actionPath == "{id}").get//new Action("/users/{id}", ctlr.getClass.getMethods()(0), ctlr, ".jsp")

    val result = buildArgs(action,request)
    assertNotNull(result)
    assertEquals(1,result.length)
    val parameters: Parameters = result(0).asInstanceOf[Parameters]
    assertEquals(1,parameters.size)
    assertEquals("10",parameters("id"))
  }

  @Test def testParseResults = {
    val request = new MockHttpServletRequest(new MockServletContext()) {
			override def getRequestDispatcher(path:String):RequestDispatcher = {
				new MockRequestDispatcher(path) {
          assertEquals("/user/view.ssp",path)
					override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ):Unit = {
						assertTrue("Correct rc attribute", fwdReq.getAttribute("rc") == null)
					}
				}
			}
		}

    val tup = ("attributeKey","attributeValue")
    val ctlr = new UserController()
    val action = ctlr.actions.find(_.actionPath == "{id}").get//new Action("/users/{id}", ctlr.getClass.getMethods()(0), ctlr, ".jsp")
    handleResults(action, tup, request, null)
    assertNotNull(request.getAttribute("attributeKey"))
  }

  @Test def testFindActionPath = {
    val context = "/home"
    val uri = "/home/users"
    val service = new BrzyServlet
    assertEquals("/users", parseActionPath(uri,context))
  }

  @Test def testFindActionPath2 = {
    val context = "/home"
    val uri = "/home/user.brzy"
    val service = new BrzyServlet
    assertEquals("/user", parseActionPath(uri,context))
  }

  @Test def testFindActionPath3 = {
    val context = ""
    val uri = "/home/10/create.brzy"
    val service = new BrzyServlet
    assertEquals("/home/10/create", parseActionPath(uri,context))
  }

  @Test def testFindActionPath4 = {
    val context = "/brzy"
    val uri = "/brzy/.brzy"
    val service = new BrzyServlet
    assertEquals("/", parseActionPath(uri,context))
  }
}