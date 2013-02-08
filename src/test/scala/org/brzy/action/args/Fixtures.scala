package org.brzy.action.args

import org.brzy.controller.BaseController
import org.brzy.action.Action
import org.brzy.action.response.{View, Model}


trait Fixtures {

  val controller = new BaseController("") {
    override val actions = List(
      action("a", a _,View("a")),
      action("b",b _,View("b")))
    def a(p:Parameters) = Model(""->"")
    def b(p:Parameters,pr:Principal) = Model(""->"")
  }

}
