package org.brzy.webapp.mock2

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.args.{ArgsBuilder, Parameters}

class SubPathControllerTest extends JUnitSuite {
  @Test def testAll() {
    val ctlr = new SubPathController()
    val action = ctlr.actions.find(_.actionPath == "{vid}").get
    assertEquals("/person/sub/view",action.defaultView)
    assertEquals("/person/100/sub/200",ArgsBuilder.parseActionPath("/person/100/sub/200.brzy",""))
    assertTrue(action.path.isMatch("/person/100/sub/200"))

    val names = action.path.parameterNames
    assertNotNull(names)
    assertEquals(2, names.size)
    assertEquals("pid", names(0))
    assertEquals("vid", names(1))

    val result = action.path.extractParameterValues("/person/100/sub/200")
    assertNotNull(result)
    assertEquals(2, result.size)
    assertEquals("100",result(0))
    assertEquals("200",result(1))
  }
}


class SubPathController extends Controller("/person/{pid}/sub") {
  val actions = List(Action("{vid}","/person/sub/view", view _))
  def view(p:Parameters) = "hello" -> "world"
}