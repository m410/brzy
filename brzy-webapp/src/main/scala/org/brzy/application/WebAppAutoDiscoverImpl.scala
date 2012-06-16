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

import org.brzy.interceptor.ProxyFactory._
import org.brzy.service.{ServiceScanner, Service}
import java.beans.ConstructorProperties
import java.lang.reflect.Constructor
import collection.mutable.{ListBuffer, WeakHashMap}
import org.brzy.webapp.controller.{ControllerScanner, Controller}
import org.slf4j.LoggerFactory

/**
 * Web Application that uses automatic discovery to find the services and controllers.
 *
 * @author Michael Fortin
 */
@deprecated("Don't use this, it's going to be removed and the application used in it's place")
class WebAppAutoDiscoverImpl(conf:WebAppConfiguration) extends WebApp(conf) {

  /**
   * it's lazy because it needs to be initialize last.  It will be initilaized once the
   * makeService method is called
   */
  protected lazy val mapOfServices = {
    val map = WeakHashMap.empty[String, AnyRef]

    val serviceClasses = ServiceScanner(conf.application.get.org.get).services
    serviceClasses.foreach(sc => {
      val clazz = sc.asInstanceOf[Class[_]]
      val instance = make(clazz, interceptor).asInstanceOf[Service]
      map += instance.serviceName -> instance
    })
    persistenceProviders.foreach(_.serviceMap.foreach(map += _))
    moduleProviders.foreach(_.serviceMap.foreach(map += _))
    map.toMap
  }

  def makeServices = mapOfServices.values.toList

  def makeControllers =  {
    val buffer = ListBuffer[Controller]()
    val controllerClasses = ControllerScanner(conf.application.get.org.get).controllers

    controllerClasses.foreach(sc => {
      val clazz = sc.asInstanceOf[Class[_]]
      val constructor: Constructor[_] = clazz.getConstructors.find(_ != null).get // should only be one

      if (constructor.getParameterTypes.length > 0) {
        val constructorArgs: Array[AnyRef] =
        if (canFindByName(clazz))
          makeArgsByName(clazz, mapOfServices)
        else
          makeArgsByType(clazz, mapOfServices)

        buffer += make(clazz, constructorArgs, interceptor).asInstanceOf[Controller]
      }
      else {
        buffer += make(clazz, interceptor).asInstanceOf[Controller]
      }
    })
    buffer.toList
  }


  protected def canFindByName(c: Class[_]): Boolean = {
    c.getAnnotation(classOf[ConstructorProperties]) != null
  }

  protected def makeArgsByName(c: Class[_], services: Map[String, AnyRef]): Array[AnyRef] = {
    val constructorProps = c.getAnnotation(classOf[ConstructorProperties])
    constructorProps.value.map(mapOfServices(_))
  }

  protected def makeArgsByType(c: Class[_], services: Map[String, AnyRef]): Array[AnyRef] = {
    val constructor: Constructor[_] = c.getConstructors.find(_ != null).get
    constructor.getParameterTypes.map((argClass: Class[_]) => {
      mapOfServices.values.find((s: AnyRef) => {
        val serviceClass =
            if (isProxy(s))
              s.getClass.getSuperclass
            else
              s.getClass
        argClass.equals(serviceClass)
      }) match {
        case Some(e) => e
        case _ =>
          LoggerFactory.getLogger(getClass).warn("No service for type '{}' on class {}",argClass, c)
          null
      }
    })
  }
}