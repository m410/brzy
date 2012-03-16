/*
 * Copyright 2001-2009 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.brzy.mod.devmode


import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.io.{PrintWriter, FileWriter, File}
import org.springframework.mock.web.{MockServletContext, MockHttpServletResponse, MockHttpServletRequest, MockServletConfig}


class BrzyAppServletSpec extends FunSuite with ShouldMatchers {

  test("An empty list should be empty") {

    // compile the code

    val servletContext = new MockServletContext()
    val servletConfig = new MockServletConfig(servletContext)
    servletConfig.addInitParameter("source_dir","/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-src")
    servletConfig.addInitParameter("classes_dir","/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-classes")
    val servlet = new BrzyAppServlet()
    servlet.init(servletConfig)
    
    val req = new MockHttpServletRequest(servletContext)
    req.setMethod("GET")
    req.setRequestURI("/.brzy")
    val res = new MockHttpServletResponse()
    
    servlet.service(req,res)
    res.getContentAsString should be("Hi there, Mike")
    changeTextFile(changed)
    Thread.sleep(100)

    val res2 = new MockHttpServletResponse()
    servlet.service(req,res2)
    res2.getContentAsString should be("Waiting")
    Thread.sleep(1000)

    val res3 = new MockHttpServletResponse()
    servlet.service(req,res3)
    res3.getContentAsString should be("Hello, Mike")
    changeTextFile(origional)
  }

  def changeTextFile(content:String) {
    val outFile = new FileWriter(new File(""))
    val out = new PrintWriter(outFile)
    out.write(content)
    out.close()
  }

  val origional = """package org.brzy.test

import org.brzy.application.WebAppConfiguration
import org.brzy.application.WebApp
import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.response.Text

class Application (config:WebAppConfiguration) extends WebApp(config) {
  def makeServices = List.empty[AnyRef]
  def makeControllers = List(proxyInstance[HomeController]()
  )
}

class HomeController extends Controller("") {
  def actions = List(Action("","",index _))
  def index() = Text("Hi there, Mike")
}"""

  val changed = """package org.brzy.test

import org.brzy.application.WebAppConfiguration
import org.brzy.application.WebApp
import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.response.Text

class Application (config:WebAppConfiguration) extends WebApp(config) {
  def makeServices = List.empty[AnyRef]
  def makeControllers = List(proxyInstance[HomeController]()
  )
}

class HomeController extends Controller("") {
  def actions = List(Action("","",index _))
  def index() = Text("Hello, Mike")
}"""

}
