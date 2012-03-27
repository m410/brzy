package org.brzy.test

import org.brzy.webapp.action.response.Text
import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.Controller


class HomeController extends Controller("") {
  def actions = List(Action("","",index _),Action("index","",index _))
  def index() = Text("Hello this is me, again... from Fredrickson")
}