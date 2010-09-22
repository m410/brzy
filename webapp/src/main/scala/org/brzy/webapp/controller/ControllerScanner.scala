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
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.scanners.TypeAnnotationsScanner

import collection.immutable.List
import collection.JavaConversions._

/**
 *   http://code.google.com/p/reflections/
 * 
 * @author Michael Fortin
 */
class ControllerScanner(val packageName:String) {

  private val reflections = new Reflections(new ConfigurationBuilder()
      .setUrls(ClasspathHelper.getUrlsForPackagePrefix(packageName))
      .setScanners(new TypeAnnotationsScanner()))

  val controllers:List[Class[_]] = reflections.getTypesAnnotatedWith(classOf[Controller]).toList
}

object ControllerScanner {
  def apply(config:String) =  new ControllerScanner(config)
}