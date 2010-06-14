package org.brzy.application


import org.brzy.action.Action
import org.brzy.interceptor.ProxyFactory._
import org.brzy.service.ServiceScanner

import org.slf4j.LoggerFactory
import java.lang.reflect.Constructor

import collection.mutable.ListBuffer
import collection.immutable.SortedSet
import org.brzy.controller.{ControllerScanner, Path, Controller}
import org.brzy.config.plugin.{Plugin, PluginResource}
import org.brzy.config.webapp.{WebAppViewResource, WebAppConfig}
import org.brzy.config.common.{Project, Application => BrzyApp}
import org.brzy.interceptor.{InterceptorResource, MethodInvoker}

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class WebApp(val config: WebAppConfig) {
  private val log = LoggerFactory.getLogger(classOf[WebApp])

  val application: BrzyApp = config.application
  val project: Project = config.project

  val viewResource: WebAppViewResource = {
    log.debug("view: {}", config.views)
    val resourceClass = Class.forName(config.views.resourceClass.get)
    val constructor: Constructor[_] = resourceClass.getConstructor(config.views.getClass)
    constructor.newInstance(config.views).asInstanceOf[WebAppViewResource]
  }

  val persistenceResources: List[PluginResource] = {
    config.persistence.map(persist => {
      log.debug("persistence: {}", persist)
      val p = persist.asInstanceOf[Plugin]
      val resourceClass = Class.forName(p.resourceClass.get)
      val constructor: Constructor[_] = resourceClass.getConstructor(p.getClass)
      constructor.newInstance(p).asInstanceOf[PluginResource]
    }).toList
  }

  val pluginResources: List[PluginResource] = {
    config.plugins.map(plugin => {
      log.debug("plugin: {}", plugin)
      val p = plugin.asInstanceOf[Plugin]
      val resourceClass = Class.forName(p.resourceClass.get)
      val constructor: Constructor[_] = resourceClass.getConstructor(p.getClass)
      constructor.newInstance(p).asInstanceOf[PluginResource]
    }).toList
  }

  val interceptor: MethodInvoker = makeInterceptor

  protected[application] def makeInterceptor: MethodInvoker = {
    val buffer = ListBuffer[MethodInvoker]()
    persistenceResources.foreach(pin => {
      if(pin.isInstanceOf[InterceptorResource])
        buffer += pin.asInstanceOf[InterceptorResource].interceptor.asInstanceOf[MethodInvoker]
    })

    if(buffer.size > 1)
      error("Currently, Only one interceptor is supported: " + buffer.mkString(", "))
    else if(buffer.size < 1)
      null
    else
      buffer(0)
  }

  val services: List[_ <: AnyRef] = makeServices

  protected[application] def makeServices: List[AnyRef] = {
    val buffer = ListBuffer[AnyRef]()

    def append(sc:AnyRef) ={
      val clazz = sc.asInstanceOf[Class[_]]
      buffer += make(clazz, interceptor)
    }

    val serviceClasses = ServiceScanner(config.application.org.get).services
    serviceClasses.foreach(append(_))
    persistenceResources.foreach(_.services.foreach(append _))
    pluginResources.foreach(_.services.foreach(append _))
    buffer.toList
  }

  val controllers: List[_ <: AnyRef] = makeControllers

  protected[application] def makeControllers: List[AnyRef] = {
    val buffer = ListBuffer[AnyRef]()
    val controllerClasses = ControllerScanner(config.application.org.get).controllers
    controllerClasses.foreach(sc => {
      val clazz = sc.asInstanceOf[Class[_]]
      buffer += make(clazz, interceptor)
    })
    buffer.toList
  }

  /**
   * The controllers are proxies so you have to get the super class
   */
  lazy val actions = {
    val list = new ListBuffer[Action]()
    controllers.foreach(ctl => {
      log.debug("load actions from controller: {}", ctl)
      val classPath = ctl.getClass.getSuperclass.getAnnotation(classOf[Controller])
      for (method <- ctl.getClass.getSuperclass.getMethods
           if method.getAnnotation(classOf[Path]) != null) {
        val methodPath = method.getAnnotation(classOf[Path])
        log.debug("controllerPath : " + classPath)
        log.debug("methodPath     : " + methodPath)

        val pathValue = classPath.value + "/" + methodPath.value
        val action = new Action(pathValue, method, ctl, viewResource.fileExtension)
        log.debug("action: " + action)
        list += action
      }
    })
    SortedSet[Action]() ++ list.toIterable
  }

  def startup = {
    viewResource.startup
    persistenceResources.foreach(_.startup)
    pluginResources.foreach(_.startup)
    viewResource.startup
    log.info("application startup")
    log.debug("services: {}",  services.mkString(","))
    log.debug("controllers: {}",  controllers.mkString(","))
    log.debug("actions: {}",  actions.mkString(","))
  }

  def shutdown = {
    viewResource.shutdown
    persistenceResources.foreach(_.shutdown)
    pluginResources.foreach(_.shutdown)
    log.info("application shutdown")
  }
}
