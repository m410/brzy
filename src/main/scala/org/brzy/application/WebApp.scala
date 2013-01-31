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

import collection.SortedSet
import collection.mutable.ListBuffer

import org.slf4j.LoggerFactory

import org.brzy.fab.interceptor.{ManagedThreadContext, InterceptorProvider}
import org.brzy.fab.mod.{RuntimeMod, ModProvider, ViewModProvider}
import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.Controller
import org.brzy.interceptor.Invoker
import org.brzy.interceptor.ProxyFactory._
import org.brzy.service.Service
import org.brzy.beanwrap.Builder
import org.brzy.webapp.action.args.{Principal, Arg, PrincipalRequest, ArgsBuilder}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import javax.servlet.FilterChain
import org.brzy.webapp.action.response._

/**
 * WebApp is short for web application.  This assembles and configures the application at
 * runtime.  It takes the web app configuration as a parameter.    This can be overriden by
 * application writers to extend it's functionality but in most cases this one will suffice.
 *
 * @param conf the configuration element for the web application.
 * 
 * @author Michael Fortin
 */
abstract class WebApp(conf: WebAppConfiguration) {

  private[this] val log = LoggerFactory.getLogger(getClass)

  val application = conf.application

  val useSsl = conf.useSsl

  def isPath(context:String, uri:String) = {
    actions.find(_.path.isMatch(ArgsBuilder.parseActionPath(uri, context))).isDefined
  }

  def wrapWithTransaction(req:HttpServletRequest,res:HttpServletResponse,chain:FilterChain) {
    val uri = req.getRequestURI
    val contextPath = req.getContextPath

    val forward =
      if (contextPath == "")
        uri.substring(0, uri.length)
      else
        uri.substring(contextPath.length, uri.length)

    log.debug("forward:{}",forward)

    interceptor.doIn(() => {
      if (forward.endsWith(".brzy"))
        chain.doFilter(req, res)
      else
        req.getRequestDispatcher(forward + ".brzy").forward(req, res)
      None // the interceptor expects a return value
    })
  }

  def callAction(req:HttpServletRequest, res:HttpServletResponse) {
    val actionPath = ArgsBuilder.parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)

    actions.find(_.path.isMatch(actionPath)) match {
      case Some(action) =>
        log.debug("{} >> {}", Array(pathLog(req) , action):_*)
        val args = ArgsBuilder(req,action)
        val principal = new PrincipalRequest(req)
        val result = callActionOrLogin(req, action, principal, args)
        ResponseHandler(action, result, req, res)
      case _ =>
        res.sendError(404)
    }
  }

  private[this] def pathLog(req:HttpServletRequest) = new StringBuilder()
          .append(req.getMethod)
          .append(":")
          .append(req.getRequestURI)
          .append(":")
          .append(if(req.getContentType != null) req.getContentType else "" )


  private[this] def callActionOrLogin(req: HttpServletRequest, action: Action, principal: Principal, args: Array[Arg]): AnyRef = {
    if (useSsl && action.requiresSsl && !req.isSecure) {
      val buf = req.getRequestURL
      // add https and remove the trailing .brzy extension
      val redirect = buf.replace(0, 4, "https").replace(buf.length() - 5, buf.length(),"").toString
      log.trace("redirect: {}",redirect)
      Redirect(redirect)
    }
    else if (action.isSecured) {
      if (req.getSession(false) != null) {
        log.trace("principal: {}",principal)

        if (action.isAuthorized(principal))
          action.execute(args, principal)
        else
          sendToAuthorization(req)
      }
      else {
        sendToAuthorization(req)
      }
    }
    else {
      action.execute(args, principal)
    }
  }

  /**
   * TODO the redirect path is hard coded here to send them to /auth, that should be configurable
   * some how.
   *
   * @param req The httpServletRequest
   * @return The redirect to the authorization page
   */
  private[this] def sendToAuthorization(req: HttpServletRequest): (Redirect, Flash, Session) = {
    val flash = Flash("Your session has ended. Please login again", "session.end")
    val sessionParam = Session("last_view" -> req.getRequestURI)
    (Redirect("/auth"), flash, sessionParam)
  }

  /**
   * The view resource provider for the application.  There is only one view provider for the
   * application, configured in a module.
   */
  val viewProvider: ViewModProvider = {
    log.debug("view: {}", conf.views.orNull)
    conf.views match {
      case Some(v) =>
        log.trace("provider: {}", v.providerClass.getOrElse("null"))
        if (v.providerClass.isDefined && v.providerClass.get != null)
          // todo fix me
          Builder[ViewModProvider]().make
//          Build.reflect[ViewModProvider](v.providerClass.get, Array(v))
        else
          null
      case _ => null
    }
  }

  /**
   * The Persistence providers for the application.  There can be more than one.
   */
  val persistenceProviders: List[ModProvider] = {
    conf.persistence.map(persist => {
      log.debug("persistence: {}", persist)
      // todo fix me
      Builder[ModProvider]().make
//      Build.reflect[ModProvider](persist.providerClass.get, Array(persist))
    }).toList
  }

  /**
   * Views and Persistence are modules, just special kinds of modules.  This allows for more
   * generic module injection into the application.  For example an email provider or jms
   * provider.
   */
  val moduleProviders: List[ModProvider] = {
    val list = ListBuffer[ModProvider]()
    conf.modules.foreach(module => {
      log.debug("module config: {}", module)

      if (module.isInstanceOf[RuntimeMod]) {
        val mod = module.asInstanceOf[RuntimeMod]

        if (mod.providerClass.isDefined)
          list += mod.newProviderInstance
      }
    })
    log.debug("modules: {}", list)
    list.toList
  }

  /**
   * This manages transaction interception for controller actions and services.  Interceptors are
   * provided by modules.
   */
  val interceptor: Invoker  = {
    val buffer = ListBuffer[ManagedThreadContext]()
    persistenceProviders.foreach(pin => {
      if (pin.isInstanceOf[InterceptorProvider])
        buffer += pin.asInstanceOf[InterceptorProvider].interceptor
    })
    new Invoker(buffer.toList)
  }

  /**
   * The service map made available to all controllers.  By default this uses class path scanning
   * to find instances of the Service trait in the classpath under the application orginization
   * package.  If you want to change how services are discovered and added to the service map,
   * override this.  To add your services manually you should wrap in the interceptor by calling
   * the instance method.
   * {{{
   *   override val services = Map(
   *     "emailService" -> instance[EmailService]
   *   )
   * }}}
   */
  val services: List[AnyRef] = makeServices


  /**
   * controllers declared as val's need to be declared lazy.
   */
  def makeServices:List[AnyRef]

  /**
   *  document me
   */
  def makeControllers:List[Controller]

  /**
   * Wrap class with AOP interceptors provided by the modules, and creates an instance of
   * the class.  This must be used to create instances of controllers and services.
   *
   * @param args the arguments for the constructor of the class.
   */
  def proxyInstance[T:Manifest](args:AnyRef*):T = {
    val clazz = manifest[T].erasure
    make(clazz, args.toArray, interceptor).asInstanceOf[T]
  }

  /**
   * The application controllers.  To change how the controllers are discovered and
   * added to the controllers list or to pragmatically add controllers to the list,
   * override this function.
   */
  val controllers: List[_ <: Controller] = makeControllers

  /**
   * Actions are lazily assembled once the application is started. The actions are collected
   * from the available controllers.
   */
  val actions = {
    val list = new ListBuffer[Action]()
    controllers.foreach((ctl:Controller) => { ctl.actions.foreach(a=> list += a)})
    SortedSet[Action]() ++ list.toIterable
  }

  /**
   * This is called by the servlet applicationContext listener to start the application.  This
   * in turn calls all the startup functions on all the modules.
   */
  def startup() {
    log.info("Startup: " + application.get.name.get + " - " + application.get.version.get)
    
    if (viewProvider != null) // may not be set for some test cases
      viewProvider.startup()
    else
      log.warn("No View Provider defined.")
    
    persistenceProviders.foreach(_.startup())
    moduleProviders.foreach(_.startup())
    services.foreach(lifeCycleCreate(_))
    services.foreach(a=>log.trace("service: {}",a))
    controllers.foreach((a:Controller)=>log.trace("controller: {}",a))
    actions.foreach(a=>log.debug("action: {}",a))
  }

  /**
   * This is called by the servlet applicationContext listener to close the application.
   * This in turn calles the shutdown methods of all the modules.
   */
  def shutdown() {
    log.info("Shutdown: " + application.get.name.get + " - " + application.get.version.get)
    services.foreach(lifeCycleDestroy(_))

    if(viewProvider != null)  // could be null in test situations
    viewProvider.shutdown()

    persistenceProviders.foreach(_.shutdown())
    moduleProviders.foreach(_.shutdown())
  }

  protected def lifeCycleCreate(service: AnyRef) {
    if(service.isInstanceOf[Service])
      service.asInstanceOf[Service].initializeService()
  }

  protected def lifeCycleDestroy(service: AnyRef) {
    if(service.isInstanceOf[Service])
      service.asInstanceOf[Service].destroyService()
  }
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