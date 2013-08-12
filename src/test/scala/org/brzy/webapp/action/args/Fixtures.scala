package org.brzy.webapp.action.args

import org.brzy.webapp.controller.BaseController
import org.brzy.webapp.action.response.{View, Model}


trait Fixtures {

  val controller = new BaseController("") {
    override val actions = List(
      action("a", a _,View("a")),
      action("b",b _,View("b")))
    def a(p:Parameters) = Model(""->"")
    def b(p:Parameters,pr:Principal) = Model(""->"")
  }

}
