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
package org.brzy.webapp.controller

import org.reflections.Reflections

import collection.immutable.List
import collection.JavaConversions._
import org.reflections.scanners.SubTypesScanner
import org.reflections.util.{ClasspathHelper, ConfigurationBuilder}
import org.slf4j.LoggerFactory

/**
 * Scans the project class hierarchy for classes that implement Controller.  This uses the
 * Reflections api under the hood.
 *
 * @see http://code.google.com/p/reflections/
 * @author Michael Fortin
 */
case class ControllerScanner(packageName:String) {

  private[this] val log = LoggerFactory.getLogger(classOf[ControllerScanner])
  
  private[this] val reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.getUrlsForPackagePrefix(packageName))
        .setScanners(new SubTypesScanner()))

  val controllers:List[Class[_]] = {
    val ctls =reflections.getSubTypesOf(classOf[Controller]).toList.filter(_.getName.indexOf("$") < 0) ++
        reflections.getSubTypesOf(classOf[CrudController[_,_]]).toList.filter(_.getName.indexOf("$") < 0)
    log.debug("controllers: {}",ctls)
    ctls
  }
}