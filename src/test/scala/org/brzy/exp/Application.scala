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

  val services = List.empty[AnyRef]

  val controllers = List.empty[Controller]

  /**
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

  val localThreadSessions:List[ManagedThreadContext] = List.empty[ManagedThreadContext]

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
