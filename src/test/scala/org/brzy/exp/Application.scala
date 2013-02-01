package org.brzy.exp

import org.slf4j.LoggerFactory
import org.brzy.fab.mod.{ViewModProvider, ModProvider}
import org.brzy.webapp.action.args.ArgsBuilder
import javax.servlet.http.HttpServletRequest
import org.brzy.application.WebAppConfiguration
import org.brzy.fab.interceptor.ManagedThreadContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class Application(conf: WebAppConfiguration) {
  private[this] val log = LoggerFactory.getLogger(getClass)

  val application = conf.application

  val useSsl = conf.useSsl

  def services = List.empty[AnyRef]

  def controllers = List.empty[Controller]

  /**
   * @param request
   * @return
   */
  def doFilterAction(request:HttpServletRequest):FilterDirect = {
    val method = request.getMethod.asInstanceOf[String]
    val contentType = request.getContentType.asInstanceOf[String]
    val path = request.getRequestURI.asInstanceOf[String]
    actions.find(_.isMatch(method, contentType, path))

    NotAnAction
  }

  def doServiceAction(request:HttpServletRequest):Option[Action] = {
    None
  }

  def moduleProviders:List[ModProvider] = List.empty[ModProvider]

  def persistenceProviders:List[ModProvider] = List.empty[ModProvider]

  def viewProvider:Option[ViewModProvider] = None

  def threadLocalSessions:List[ManagedThreadContext] = List.empty[ManagedThreadContext]

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
