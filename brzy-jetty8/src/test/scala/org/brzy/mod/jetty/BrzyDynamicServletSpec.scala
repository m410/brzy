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
package org.brzy.mod.jetty

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import java.io.{PrintWriter, FileWriter, File}
import org.springframework.mock.web.{MockServletContext, MockHttpServletResponse, MockHttpServletRequest, MockServletConfig}
import tools.nsc.reporters.ConsoleReporter
import tools.nsc.{Settings, Global}


class BrzyDynamicServletSpec extends FunSuite with ShouldMatchers {

  test("Call action, change source, and show new action") {

    // set the precondition
    changeSourceFile(origional)

    preCompile(List(
      new File(sourceDir + "org/brzy/test/Application.scala"),
      new File(sourceDir + "org/brzy/test/HomeController.scala"),
      new File(sourceDir + "org/brzy/test/ApplicationLoader.scala")
    ))

    val servletContext = new MockServletContext()
    val servletConfig = new MockServletConfig(servletContext)
    servletConfig.addInitParameter("source_dir",sourceDir)
    servletConfig.addInitParameter("classes_dir",classesDir)
    servletConfig.addInitParameter("compiler_path",cpath.foldLeft("")((r,c) => r+":"+c))
    val servlet = new BrzyDynamicServlet()
    servlet.init(servletConfig)
    
    val req = new MockHttpServletRequest(servletContext)
    req.setMethod("GET")
    req.setRequestURI("/.brzy")
    val res = new MockHttpServletResponse()
    
    servlet.service(req,res)
    res.getContentAsString should be("Hi there, Mike")
    changeSourceFile(changed)
    Thread.sleep(2000)

    val res2 = new MockHttpServletResponse()
    servlet.service(req,res2)
    res2.getContentAsString should be("Waiting")
    Thread.sleep(4000)

    val res3 = new MockHttpServletResponse()
    servlet.service(req,res3)
    res3.getContentAsString should be("Hello, Mike")

    // reset back to original
    changeSourceFile(origional)
  }

  private[this] val sourceDir = "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-src/"
  private[this] val classesDir = "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-classes/"


  private[this] def changeSourceFile(content:String) {
    val f = "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-src/org/brzy/test/HomeController.scala"
    val file = new File(f)
    val outFile = new FileWriter(file)
    val out = new PrintWriter(outFile)
    out.write(content)
    out.close()
  }

  private[this] val origional = """package org.brzy.test
import org.brzy.webapp.action.response.Text
import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.Controller
class HomeController extends Controller("") {
  def actions = List(Action("","",index _))
  def index() = Text("Hi there, Mike")
}
"""

  private[this] val changed = """package org.brzy.test
import org.brzy.webapp.action.response.Text
import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.Controller
class HomeController extends Controller("") {
  def actions = List(Action("","",index _))
  def index() = Text("Hello, Mike")
}
"""


  private def preCompile(files: List[File]) {
    def error(s: String) {
      println(s)
    }
    val settings = new Settings(error)
    settings.classpath.value = cpath.foldLeft("")((r,c) => r+":"+c)
    settings.sourcedir.value = sourceDir
    settings.outdir.value = classesDir
    settings.deprecation.value = true // enable detailed deprecation warnings
    settings.unchecked.value = true // enable detailed unchecked warnings

    val reporter = new ConsoleReporter(settings)
    val compiler = new Global(settings, reporter)
    (new compiler.Run).compile(files.map(_.getAbsolutePath))

    reporter.printSummary()
    if (reporter.hasErrors || reporter.WARNING.count > 0) {
        //      ...
    }
  }

  private def cpath = List(
      classesDir,
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/aspectjweaver-1.6.8.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/beanwrap-0.2.2.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/brzy-scalate-1.0.0.beta2.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/brzy-webapp-1.0.0.beta3.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-fileupload-1.2.2.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-io-1.4.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-logging-1.1.1.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/dom4j-1.6.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/fab-configuration-0.8.1.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/google-collections-1.0.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/gson-1.4.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/guava-r09.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/ivy-2.2.0.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/javassist-3.11.0.GA.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/json-1.1.1.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/logback-classic-0.9.27.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/logback-core-0.9.27.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/paranamer-2.3.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/reflections-0.9.5-RC2.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scala-compiler-2.8.2.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scala-library-2.8.2.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scalabeans_2.8.1-0.2.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/servlet-api-6.0.29.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/slf4j-api-1.6.1.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/snakeyaml-1.7.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validation-api-1.0.0.GA.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validator-0.1.jar",
      "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/xml-apis-1.0.b2.jar"
    )
}
