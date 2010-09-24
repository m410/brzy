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
import collection.mutable.{WeakHashMap, ListBuffer}

import org.slf4j.LoggerFactory
import javassist.util.proxy.ProxyObject
import java.lang.reflect.Constructor
import java.beans.ConstructorProperties

import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.{ControllerScanner, Controller, Action => ActionAnnotation}
import org.brzy.fab.interceptor.{ManagedThreadContext, InterceptorProvider}
import org.brzy.webapp.interceptor.Invoker
import org.brzy.webapp.interceptor.ProxyFactory._

import org.brzy.fab.reflect.Construct

import org.brzy.service.{ServiceScanner, PostCreate, PreDestroy}
import org.brzy.fab.mod.{RuntimeMod, ModProvider, ViewModProvider}

/**
 *
 */
class WebApp(conf: WebAppConf) {
  private val log = LoggerFactory.getLogger(getClass)

  val application = conf.application
  val project = conf.project

  val viewResource: ViewModProvider = {
    log.debug("view: {}", conf.views)
    log.trace("resource: {}", conf.views.resourceClass.getOrElse("null"))
    if (conf.views.resourceClass.isDefined && conf.views.resourceClass.get != null)
      Construct[ViewModProvider](conf.views.resourceClass.get, Array(conf.views))
    else
      null
  }

  val persistenceResources: List[ModProvider] = {
    conf.persistence.map(persist => {
      log.debug("persistence: {}", persist)
      Construct[ModProvider](persist.resourceClass.get, Array(persist))
    }).toList
  }

  val moduleResource: List[ModProvider] = {
    val list = ListBuffer[ModProvider]()
    conf.modules.foreach(module => {
      log.debug("module: {}", module)
      val mod = module.asInstanceOf[RuntimeMod]

      if (mod.resourceClass.isDefined && mod.resourceClass.get != null) {
        list += Construct[ModProvider](mod.resourceClass.get, Array(mod))
      }
    })
    list.toList
  }

  val interceptor: Invoker = makeInterceptor

  protected[application] def makeInterceptor: Invoker = {
    val buffer = ListBuffer[ManagedThreadContext]()
    persistenceResources.foreach(pin => {
      if (pin.isInstanceOf[InterceptorProvider])
        buffer += pin.asInstanceOf[InterceptorProvider].interceptor
    })
    new Invoker(buffer.toList)
  }

  val serviceMap: Map[String, _ <: AnyRef] = makeServiceMap

  protected[application] def makeServiceMap: Map[String, _ <: AnyRef] = {
    val map = WeakHashMap.empty[String, AnyRef]

    def name(c: Class[_]): String = { // TODO also need to pull name from annotation
      val in = c.getName
      in.charAt(0).toLower + in.substring(1, in.length)
    }

    val serviceClasses = ServiceScanner(conf.application.org.get).services
    serviceClasses.foreach(sc => {
      val clazz = sc.asInstanceOf[Class[_]]
      map += name(clazz) -> make(clazz, interceptor)
    })
    persistenceResources.foreach(_.serviceMap.foreach(map + _))
    moduleResource.foreach(_.serviceMap.foreach(map + _))
    map.toMap
  }

  val controllers: List[_ <: AnyRef] = makeControllers

  protected[application] def makeControllers: List[AnyRef] = {
    val buffer = ListBuffer[AnyRef]()
    val controllerClasses = ControllerScanner(conf.application.org.get).controllers
    controllerClasses.foreach(sc => {
      val clazz = sc.asInstanceOf[Class[_]]
      val constructor: Constructor[_] = clazz.getConstructors.find(_ != null).get // should only be one

      if (constructor.getParameterTypes.length > 0) {
        val constructorArgs: Array[AnyRef] =
        if (canFindByName(clazz))
          makeArgsByName(clazz, serviceMap)
        else
          makeArgsByType(clazz, serviceMap)

        buffer += make(clazz, constructorArgs, interceptor)
      }
      else {
        buffer += make(clazz, interceptor)
      }
    })
    buffer.toList
  }

  protected[application] def canFindByName(c: Class[_]): Boolean = {
    c.getAnnotation(classOf[ConstructorProperties]) != null
  }

  protected[application] def makeArgsByName(c: Class[_], services: Map[String, AnyRef]): Array[AnyRef] = {
    val constructorProps = c.getAnnotation(classOf[ConstructorProperties])
    constructorProps.value.map(services(_))
  }

  protected[application] def makeArgsByType(c: Class[_], services: Map[String, AnyRef]): Array[AnyRef] = {
    val constructor: Constructor[_] = c.getConstructors.find(_ != null).get
    constructor.getParameterTypes.map((argClass: Class[_]) => {
      serviceMap.values.find((s: AnyRef) => {
        val serviceClass =
        if (isProxy(s))
          s.getClass.getSuperclass
        else
          s.getClass
        argClass.equals(serviceClass)
      }).get
    })
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
           if method.getAnnotation(classOf[ActionAnnotation]) != null) {
        val methodPath = method.getAnnotation(classOf[ActionAnnotation])
        log.debug("controllerPath : " + classPath)
        log.debug("methodPath     : " + methodPath)

        val pathValue =
            if (methodPath.value.equals(""))
              classPath.value
            else
              classPath.value + "/" + methodPath.value

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
    moduleResource.foreach(_.startup)
    viewResource.startup
    serviceMap.values.foreach(lifeCycleCreate(_))
    log.info("application  : startup")
    log.debug("service map : {}", serviceMap.mkString(","))
    log.debug("controllers : {}", controllers.mkString(","))
    log.debug("actions     : {}", actions.mkString(","))
  }

  def shutdown = {
    serviceMap.values.foreach(lifeCycleDestroy(_))
    viewResource.shutdown
    persistenceResources.foreach(_.shutdown)
    moduleResource.foreach(_.shutdown)
    log.info("application shutdown")
  }

  protected[application] def lifeCycleCreate(service: AnyRef) = {
    val clazz =
    if (service.isInstanceOf[ProxyObject]) service.getClass.getSuperclass
    else service.getClass

    val option = clazz.getMethods.find(_.getAnnotation(classOf[PostCreate]) != null)
    option match {
      case Some(m) => m.invoke(service, Array(): _*)
      case _ =>
    }
  }

  protected[application] def lifeCycleDestroy(service: AnyRef) = {
    val clazz =
    if (service.isInstanceOf[ProxyObject]) service.getClass.getSuperclass
    else service.getClass

    val option = clazz.getMethods.find(_.getAnnotation(classOf[PreDestroy]) != null)
    option match {
      case Some(m) => m.invoke(service, Array(): _*)
      case _ =>
    }
  }
}

/**
 *
 */
object WebApp {
  private val log = LoggerFactory.getLogger(getClass)

  def apply(env: String): WebApp = apply(WebAppConf(env))

  def apply(config: WebAppConf): WebApp = {
    log.debug("application class: {}", config.application.applicationClass.getOrElse("NA"))

    if (config.application.applicationClass.isDefined)
      Construct[WebApp](config.application.applicationClass.get, Array(config))
    else
      new WebApp(config)
  }

}