package org.brzy.webapp.mock2

import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.webapp.action.args.ArgsBuilder

class HomeControllerTest extends JUnitSuite {
  @Test def testPathHome() {
    val ctlr = new HomeController()
    val action = ctlr.actions.find(_.actionPath == "").get
    assertEquals("/index",action.defaultView)
    assertEquals("/",ArgsBuilder.parseActionPath("/.brzy",""))
    assertTrue(action.path.isMatch("/"))
    
    val result = action.path.extractParameterValues("/")
    assertNotNull(result)
    assertEquals(0, result.size)
  }
}

class HomeController extends Controller("") {
  val actions = List(Action("","/index", index _))
  def index = "hello" -> "world"
}