/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.application

import org.slf4j.LoggerFactory

import org.brzy.fab.mod.{ModProvider, ViewModProvider}
import javax.servlet.http.HttpServletRequest

import org.brzy._
import action.args.{PrincipalRequest, ArgsBuilder}
import fab.interceptor.ThreadContextSessionFactory
import org.brzy.controller.Controller
import org.brzy.action.Action
import org.brzy.ActOnAsync


/**
 * WebApp is short for web application.  This assembles and configures the application at
 * runtime.  It takes the web app configuration as a parameter.    This can be overriden by
 * application writers to extend it's functionality but in most cases this one will suffice.
 *
 * @param conf the configuration element for the web application.
 * 
 * @author Michael Fortin
 */
class WebApp(conf: WebAppConfiguration) {

  private val log = LoggerFactory.getLogger(getClass)

  /**
   * The application class, hold information like the application name and version.
   */
  val application = conf.application

  /**
   * A flag to tell if ssl is enabled.  It can be turned on and off for different environments.
   */
  val useSsl = conf.useSsl

  /**
   * The services for the application.  It's for things like jms listeners and cron schedulers.
   */
  def services = List.empty[AnyRef]

  /**
   * The controllers for the application.
   * @see org.brzy.webapp.controller.controller
   * @return
   */
  def controllers = List.empty[Controller]

  /**
   * @param request
   * @return
   */
  def doFilterAction(request:HttpServletRequest):FilterDirect = {
    val method = request.getMethod
    val contentType = request.getContentType
    val actionPath = ArgsBuilder.parseActionPath(request.getRequestURI, request.getContextPath)

    log.debug("method:{}", method)
    log.debug("contentType:{}", contentType)
    log.debug("actionPath:{}", actionPath)

    actions.find(_.isMatch(method, contentType, actionPath.path)) match  {
      case Some(action) =>

        if (action.requiresSsl && !request.isSecure)
          RedirectToSecure(request)
        else if (!action.isAuthorized(new PrincipalRequest(request)))
          RedirectToAuthenticate("/auth")
        else if (actionPath.isServlet && !actionPath.isAsync && !action.async)
          ActOn(action)
        else if (actionPath.isServlet && actionPath.isAsync && action.async)
          ActOnAsync(action)
        else if (action.async)
          DispatchTo(actionPath.path + ".brzy_async")
        else
          DispatchTo(actionPath.path + ".brzy")

      case _ =>
        NotAnAction
    }
  }

  /**
   *
   * @param request
   * @return
   */
  def serviceAction(request:HttpServletRequest):Option[Action] = {

    if (log.isDebugEnabled)
      log.debug(pathLog(request))

    val actionPath = ArgsBuilder.parseActionPath(request.getRequestURI, request.getContextPath)
    val contentType = request.getHeader("Content-Type")
    val method = request.getMethod
    actions.find(_.isMatch(method, contentType, actionPath.path))
  }

    private[this] def pathLog(req:HttpServletRequest) = new StringBuilder()
        .append(req.getMethod)
        .append(":")
        .append(req.getRequestURI)
        .append(":")
        .append(if(req.getContentType != null) req.getContentType else "" )
        .toString()

  def moduleProviders:List[ModProvider] = List.empty[ModProvider]

  def persistenceProviders:List[ModProvider] = List.empty[ModProvider]

  def viewProvider:Option[ViewModProvider] = None

  def threadLocalSessions:List[ThreadContextSessionFactory] = List.empty[ThreadContextSessionFactory]

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


/**
 *  This is a factory class to assemble the application at runtime.
 */
object WebApp {
  private val log = LoggerFactory.getLogger(getClass)

  def apply(env: String): WebApp = apply(WebAppConfiguration.runtime(env))

  def apply(config: WebAppConfiguration): WebApp = {
    log.debug("application class: {}", config.application.get.applicationClass.getOrElse("NA"))
    val projectApplicationClass = config.application.get.applicationClass.get
//    Build.reflect[WebApp](projectApplicationClass, Array(config))
    val args = Array(config)
    val c = Class.forName(projectApplicationClass)
    val constructor = c.getConstructor(args.map(_.getClass):_*)
    constructor.newInstance(args:_*).asInstanceOf[WebApp]



  }

}