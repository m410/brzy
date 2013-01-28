package org.brzy.application

import org.slf4j.LoggerFactory
import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.fab.mod.{ViewModProvider, ModProvider}
import org.brzy.webapp.action.args.ArgsBuilder
import org.brzy.interceptor.Invoker
import javax.servlet.http.HttpServletRequest

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class Application(conf: WebAppConfiguration) {
  private[this] val log = LoggerFactory.getLogger(getClass)

  val application = conf.application

  val useSsl = conf.useSsl

  val services = List.empty[AnyRef]

  val controllers = List.empty[Controller]

  /**
   * in filter
   * application.actionFor(request) match {
   *   case Some(a) =>
   *     if(isInternalPath(request))
   *      a.transaction.doIn {()=> chain.doFilter(req,res) }
   *     else
   *      req.getRequestDispatcher(forward + ".brzy").forward(req, res)
   *   case _ =>
   *     chain.doFilter(req,res)
   *  }
   *
   *  application.actionFor(request) match {
   *   case Some(a) =>
   *   if(a.isSecure(request))
   *   if(a.requiredAuthentication(request) && a.authenticated(request))
   *    if(a.isAuthorized(request))
   *      val result = a.execute(request,response)
          ResponseHandler(action, result, request, response)
   *    else
   *   case _ =>
   *  }
   *
   * @param request
   * @return
   */
  def actionFor(request:HttpServletRequest):Option[Action] = {
    val path = ArgsBuilder.parseActionPath(request.getRequestURI, request.getContextPath)
    actions.find(_.path.isMatch(path))
  }

  val moduleProviders:List[ModProvider] = List.empty[ModProvider]

  val persistenceProviders:List[ModProvider] = List.empty[ModProvider]

  val viewProvider:Option[ViewModProvider] = None

  val interceptors:List[Invoker] = List.empty[Invoker]

  /**
   * Actions are lazily assembled once the application is started. The actions are collected
   * from the available controllers.
   */
  lazy val actions = {
    controllers.flatMap((ctl:Controller) => { ctl.actions}).sorted.toSeq
  }

  def startup(){}

  def shutdown(){}
}
