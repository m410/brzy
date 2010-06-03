package org.brzy.application


import org.brzy.action.Action
import org.brzy.interceptor.ProxyFactory._
import org.brzy.service.ServiceScanner

import org.slf4j.LoggerFactory
import java.lang.reflect.Constructor

import collection.mutable.{ArrayBuffer, ListBuffer}
import collection.immutable.SortedSet
import org.brzy.controller.{ControllerScanner, Path, Controller}
import org.brzy.interceptor.{MethodInvoker}
import org.brzy.config.plugin.{Plugin, PluginResource}
import org.brzy.config.webapp.{WebAppViewResource, WebAppConfig}

/**
 * @author Michael Fortin
 * @version $Id : $
 */
abstract class WebApp(val config: WebAppConfig) {
  private val log = LoggerFactory.getLogger(classOf[WebApp])

  val viewPluginResource:WebAppViewResource = {
    val resourceClass = Class.forName(config.views.resourceClass.get)
    val constructor: Constructor[_] = resourceClass.getConstructor(config.views.getClass)
    constructor.newInstance(config.views).asInstanceOf[WebAppViewResource]
  }

  val persistencePluginResources:Array[PluginResource] = {
    config.persistence.map(persist => {
      val p = persist.asInstanceOf[Plugin]
      val resourceClass = Class.forName(p.resourceClass.get)
      val constructor: Constructor[_] = resourceClass.getConstructor(p.getClass)
      constructor.newInstance(p).asInstanceOf[PluginResource]
    }).toArray
  }

  val pluginResources:Array[PluginResource] = {
    config.plugins.map(plugin => {
      val p = plugin.asInstanceOf[Plugin]
      val resourceClass = Class.forName(p.resourceClass.get)
      val constructor: Constructor[_] = resourceClass.getConstructor(p.getClass)
      constructor.newInstance(p).asInstanceOf[PluginResource]
    }).toArray
  }

  val interceptor:MethodInvoker = {
    val buffer = ArrayBuffer[MethodInvoker]()
    persistencePluginResources.foreach(pin => pin.interceptors.foreach(p => buffer += p.asInstanceOf[MethodInvoker]))
    val array = buffer.toArray
    array(0)  // TODO this only returns the first interceptor, and ignores the rest
  }

  val services: Array[_ <: AnyRef] = makeServices

  protected def makeServices:Array[AnyRef] = {
    val buffer = ArrayBuffer[AnyRef]()
    val serviceClasses = ServiceScanner(config.application.org.get).services
    serviceClasses.foreach(sc => {
      val clazz = sc.asInstanceOf[Class[_]]
      buffer += make(clazz, interceptor)})
    buffer.toArray[AnyRef]
  }

  val controllers: Array[_ <: AnyRef] = makeControllers

  protected def makeControllers:Array[AnyRef]= {
    val buffer = ArrayBuffer[AnyRef]()
    val serviceClasses = ControllerScanner(config.application.org.get).controllers
    serviceClasses.foreach(sc =>{
      val clazz = sc.asInstanceOf[Class[_]]
      // TODO inject services
      buffer += make(clazz, interceptor)})
    buffer.toArray[AnyRef]
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
        val action = new Action(pathValue, method, ctl, viewPluginResource.fileExtension) 
        log.debug("action: " + action)
        list += action
      }
    })
    SortedSet[Action]() ++ list.toIterable
  }

  def startup = {
    viewPluginResource.startup
    persistencePluginResources.foreach(_.startup)
    pluginResources.foreach(_.startup)
    viewPluginResource.startup
    log.info("application startup")
  }

  def shutdown = {
    viewPluginResource.shutdown
    persistencePluginResources.foreach(_.shutdown)
    pluginResources.foreach(_.shutdown)
    log.info("application shutdown")
  }
}
