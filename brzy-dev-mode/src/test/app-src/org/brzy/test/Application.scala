package org.brzy.test

import org.brzy.application.WebAppConfiguration
import org.brzy.application.WebApp
import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.args.{Principal, Arg, PrincipalRequest, ArgsBuilder}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.brzy.webapp.action.response.{ResponseHandler, Text}

class Application (config:WebAppConfiguration) extends WebApp(config) {
  def makeServices = List.empty[AnyRef]
  def makeControllers = List(proxyInstance[HomeController]()
  )

  // TODO Move to helper class
  override def actionForPath(actionPath:String) = actions.find(_.path.isMatch(actionPath))

  // TODO Move to helper class
  override def actionArguments(req:HttpServletRequest, action:Action) = ArgsBuilder(req,action)

  // TODO Move to helper class
  override def principal(req:HttpServletRequest) = new PrincipalRequest(req)

  // TODO Move to helper class
  override def executeAction(action:Action, args:Array[Arg],principal:Principal) = action.execute(args, principal)

  override def response(action:Action, result:AnyRef, req:HttpServletRequest, res:HttpServletResponse) {
    ResponseHandler(action, result, req, res)
  }

}

class HomeController extends Controller("") {
  def actions = List(Action("","",index _))
  def index() = Text("Hi there, Mike")
}