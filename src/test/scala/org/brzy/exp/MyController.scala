package org.brzy.exp

import org.brzy.webapp.action.response.{Model, Response}


class MyController extends Controller("home"){ self:PersonStore =>

  override def actions = super.actions ++ List(
    Action("{id}",get _)
  )

  def get() = {
    val m = Model("name"->person(1))
    Array.empty[Response]
  }
}
