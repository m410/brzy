package org.brzy.application


import org.brzy.action.Action
import org.brzy.interceptor.{InterceptorResource, ManagedThreadContext, Invoker}
import org.brzy.interceptor.ProxyFactory._
import org.brzy.service.ServiceScanner

import org.slf4j.LoggerFactory
import java.lang.reflect.Constructor

import collection.mutable.ListBuffer
import collection.immutable.SortedSet
import org.brzy.controller.{ControllerScanner, Path, Controller}
import org.brzy.config.mod.{Mod, ModResource}
import org.brzy.config.webapp.{WebAppViewResource, WebAppConfig}
import org.brzy.config.common.{Project, Application => BrzyApp}

/**
 * @author Michael Fortin
 */
class WebApp(val config: WebAppConfig) {
  private val log = LoggerFactory.getLogger(classOf[WebApp])

  val application: BrzyApp = config.application
  val project: Project = config.project

  val viewResource: WebAppViewResource = {
    log.debug("view: {}", config.views)
    if (config.views.resourceClass.isDefined) {
      val resourceClass = Class.forName(config.views.resourceClass.get)
      val constructor: Constructor[_] = resourceClass.getConstructor(config.views.getClass)
      constructor.newInstance(config.views).asInstanceOf[WebAppViewResource]
    }
    else {
      null
    }
  }

  val persistenceResources: List[ModResource] = {
    config.persistence.map(persist => {
      log.debug("persistence: {}", persist)
      val mod = persist.asInstanceOf[Mod]
      val resourceClass = Class.forName(mod.resourceClass.get)
      val constructor: Constructor[_] = resourceClass.getConstructor(mod.getClass)
      constructor.newInstance(mod).asInstanceOf[ModResource]
    }).toList
  }

  val moduleResource: List[ModResource] = {
    val list = ListBuffer[ModResource]()
    config.modules.foreach(module => {
      log.debug("module: {}", module)
      val mod = module.asInstanceOf[Mod]
      if (mod.resourceClass.isDefined && mod.resourceClass.get != null) {
        val resourceClass = Class.forName(mod.resourceClass.get)
        val constructor: Constructor[_] = resourceClass.getConstructor(mod.getClass)
        list += constructor.newInstance(mod).asInstanceOf[ModResource]
      }
    })
    list.toList
  }

  val interceptor: Invoker = makeInterceptor

  protected[application] def makeInterceptor: Invoker = {
    val buffer = ListBuffer[ManagedThreadContext]()
    persistenceResources.foreach(pin => {
      if (pin.isInstanceOf[InterceptorResource])
        buffer += pin.asInstanceOf[InterceptorResource].interceptor
    })

    if (buffer.size > 1)
      error("Currently, Only one interceptor is supported: " + buffer.mkString(", "))
    else if (buffer.size < 1)
      null
    else
      buffer(0)
    new Invoker(buffer.toList)
  }

  val services: List[_ <: AnyRef] = makeServices

  protected[application] def makeServices: List[AnyRef] = {
    val buffer = ListBuffer[AnyRef]()

    def append(sc: AnyRef) = {
      val clazz = sc.asInstanceOf[Class[_]]
      buffer += make(clazz, interceptor)

    }

    val serviceClasses = ServiceScanner(config.application.org.get).services
    serviceClasses.foreach(append(_))
    persistenceResources.foreach(_.services.foreach(append _))
    moduleResource.foreach(_.services.foreach(append _))
    buffer.toList
  }

  val controllers: List[_ <: AnyRef] = makeControllers

  protected[application] def makeControllers: List[AnyRef] = {
    val buffer = ListBuffer[AnyRef]()
    val controllerClasses = ControllerScanner(config.application.org.get).controllers
    controllerClasses.foreach(sc => {
      val clazz = sc.asInstanceOf[Class[_]]
      val constructor: Constructor[_] = clazz.getConstructors.find(_ != null).get // should only be one

      if (constructor.getParameterTypes.length > 0) {
        val constructorArgs = constructor.getParameterTypes.map((argClass:Class[_]) => {
          val list = services.filter((s: AnyRef) => { // work-around because find does not compile
            val serviceClass =
                if (isProxy(s))
                  s.getClass.getSuperclass
                else
                  s.getClass
            argClass.equals(serviceClass)
          })

          if(list.size == 1)
            list(0)
          else
            error("No Service for class: " + argClass)
        })
        buffer += make(clazz, constructorArgs, interceptor)
      }
      else {
        buffer += make(clazz, interceptor)
      }
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
    moduleResource.foreach(_.startup)
    viewResource.startup
    log.info("application startup")
    log.debug("services: {}", services.mkString(","))
    log.debug("controllers: {}", controllers.mkString(","))
    log.debug("actions: {}", actions.mkString(","))
  }

  def shutdown = {
    viewResource.shutdown
    persistenceResources.foreach(_.shutdown)
    moduleResource.foreach(_.shutdown)
    log.info("application shutdown")
  }
}
