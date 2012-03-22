package org.brzy.test

import org.brzy.webapp.action.response.Text
import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.Controller


class HomeController extends Controller("") {
  def actions = List(Action("","",index _))
  def index() = Text("Hi there, Mike")
}