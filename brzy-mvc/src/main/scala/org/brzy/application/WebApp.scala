package org.brzy.application


import org.brzy.mvc.action.Action
import org.brzy.mvc.interceptor.{InterceptorResource, ManagedThreadContext, Invoker}
import org.brzy.mvc.controller.{ControllerScanner, Path, Controller}
import org.brzy.mvc.interceptor.ProxyFactory._

import org.brzy.config.mod.{Mod, ModResource}
import org.brzy.config.webapp.{WebAppViewResource, WebAppConfig}
import org.brzy.config.common.{Project, Application => BrzyApp}

import org.slf4j.LoggerFactory

import collection.immutable.SortedSet
import collection.mutable.{WeakHashMap, ListBuffer}
import java.beans.ConstructorProperties
import java.lang.reflect.Constructor
import javassist.util.proxy.ProxyObject
import org.brzy.service.{PreDestroy, PostCreate, ServiceScanner}

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
    new Invoker(buffer.toList)
  }

  val serviceMap: Map[String, _ <: AnyRef] = makeServiceMap

  protected[application] def makeServiceMap:Map[String, _ <: AnyRef] = {
    val map = WeakHashMap.empty[String, AnyRef]

    def name(c:Class[_]):String = { // TODO also need to pull name from annotation
      val in = c.getName
      in.charAt(0).toLower + in.substring(1,in.length)
    }

    val serviceClasses = ServiceScanner(config.application.org.get).services
    serviceClasses.foreach(sc=>{
      val clazz = sc.asInstanceOf[Class[_]]
      map += name(clazz)->make(clazz, interceptor)
    })
    persistenceResources.foreach(_.serviceMap.foreach(map + _))
    moduleResource.foreach(_.serviceMap.foreach(map + _))
    map.toMap
  }

  val controllers: List[_ <: AnyRef] = makeControllers

  protected[application] def makeControllers: List[AnyRef] = {
    val buffer = ListBuffer[AnyRef]()
    val controllerClasses = ControllerScanner(config.application.org.get).controllers
    controllerClasses.foreach(sc => {
      val clazz = sc.asInstanceOf[Class[_]]
      val constructor: Constructor[_] = clazz.getConstructors.find(_ != null).get // should only be one

      if (constructor.getParameterTypes.length > 0) {
        val constructorArgs:Array[AnyRef] =
            if(canFindByName(clazz))
              makeArgsByName(clazz,serviceMap)
            else
              makeArgsByType(clazz,serviceMap)

        buffer += make(clazz, constructorArgs, interceptor)
      }
      else {
        buffer += make(clazz, interceptor)
      }
    })
    buffer.toList
  }

  protected[application] def canFindByName(c:Class[_]): Boolean = {
    c.getAnnotation(classOf[ConstructorProperties]) != null
  }

  protected[application] def makeArgsByName(c:Class[_],services:Map[String,AnyRef]): Array[AnyRef] = {
    val constructorProps = c.getAnnotation(classOf[ConstructorProperties])
    constructorProps.value.map(services(_))
  }

  protected[application] def makeArgsByType(c: Class[_], services: Map[String, AnyRef]): Array[AnyRef] = {
    val constructor: Constructor[_] = c.getConstructors.find(_ != null).get
    constructor.getParameterTypes.map((argClass: Class[_]) => {
      serviceMap.values.find((s:AnyRef)=> {
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

  protected[application] def lifeCycleCreate(service:AnyRef) = {
    val clazz =
      if (service.isInstanceOf[ProxyObject]) service.getClass.getSuperclass
      else service.getClass

    val option = clazz.getMethods.find(_.getAnnotation(classOf[PostCreate]) != null)
    option match {
      case Some(m) => m.invoke(service, Array():_*)
      case _ =>
    }
  }

  protected[application] def lifeCycleDestroy(service:AnyRef) = {
    val clazz =
      if (service.isInstanceOf[ProxyObject]) service.getClass.getSuperclass
      else service.getClass

    val option = clazz.getMethods.find(_.getAnnotation(classOf[PreDestroy]) != null)
    option match {
      case Some(m) => m.invoke(service, Array():_*)
      case _ =>
    }
  }
}
