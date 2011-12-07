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
import org.brzy.beanwrap.Build

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

  val log = LoggerFactory.getLogger(getClass)

  val application = conf.application

  val useSsl = conf.useSsl.getOrElse(false)

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
          Build.reflect[ViewModProvider](v.providerClass.get, Array(v))
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
      Build.reflect[ModProvider](persist.providerClass.get, Array(persist))
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
   *   override val serviceMap = Map(
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
    controllers.foreach(ctl => { ctl.actions.foreach(a=> list += a)})
    SortedSet[Action]() ++ list.toIterable
  }

  /**
   * This is called by the servlet applicationContext listener to start the application.  This
   * in turn calls all the startup functions on all the modules.
   */
  def startup() {
    log.info("Startup: " + application.get.name.get + " - " + application.get.version.get)
    viewProvider.startup()
    persistenceProviders.foreach(_.startup())
    moduleProviders.foreach(_.startup())
    viewProvider.startup()
    services.foreach(lifeCycleCreate(_))
    services.foreach(a=>log.trace("service: {}",a))
    controllers.foreach(a=>log.trace("controller: {}",a))
    actions.foreach(a=>log.debug("action: {}",a))
  }

  /**
   * This is called by the servlet applicationContext listener to close the application.
   * This in turn calles the shutdown methods of all the modules.
   */
  def shutdown() {
    log.info("Shutdown: " + application.get.name.get + " - " + application.get.version.get)
    services.foreach(lifeCycleDestroy(_))
    viewProvider.shutdown()
    persistenceProviders.foreach(_.shutdown())
    moduleProviders.foreach(_.shutdown())
  }

  protected[application] def lifeCycleCreate(service: AnyRef) {
    if(service.isInstanceOf[Service])
      service.asInstanceOf[Service].initializeService()
  }

  protected[application] def lifeCycleDestroy(service: AnyRef) {
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

    if (config.application.get.applicationClass.isDefined) {
      val projectApplicationClass = config.application.get.applicationClass.get
      Build.reflect[WebApp](projectApplicationClass, Array(config))
    }
    else
      new WebAppAutoDiscoverImpl(config)
  }

}