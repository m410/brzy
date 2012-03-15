package org.brzy.test

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
}