package org.brzy.webapp.action.args

import org.scalatest.FlatSpec
import org.springframework.mock.web.{MockServletContext, MockHttpServletRequest}
import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.response.Model


/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class ArgsBuilderSpec extends FlatSpec {
  val controller = new Controller("") {
    def actions = List(Action("a","a", a _),Action("b","b", b _))
    def a(p:Parameters) = Model(""->"")
    def b(p:Parameters,pr:Principal) = Model(""->"")
  }

  behavior of "A ArgsBuilder"

  it should "create parameters" in {
    val request = new MockHttpServletRequest(new MockServletContext())
    val args = ArgsBuilder(request,controller.actions(0))
    assert(args.length == 1)
    assert(args(0).isInstanceOf[Parameters])
  }

  it should "create parameters and pricipal" in  {
    val request = new MockHttpServletRequest(new MockServletContext())
    val args = ArgsBuilder(request,controller.actions(1))
    assert(args.length == 2)
    assert(args(0).isInstanceOf[Parameters])
    assert(args(1).isInstanceOf[Principal])
  }
}