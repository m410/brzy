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
package org.brzy.service

import org.reflections.Reflections
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}

import scala.collection.JavaConversions._

/**
 * Used by the WebApp to scan for services in the classpath.
 * 
 * @author Michael Fortin
 */
class ServiceScanner(val packageName:String) {

  val reflections = new Reflections(new ConfigurationBuilder()
        .setUrls(ClasspathHelper.getUrlsForPackagePrefix(packageName))
        .setScanners(
            new ResourcesScanner(),
            new TypeAnnotationsScanner(),
            new SubTypesScanner()))

  val services = asSet(reflections.getTypesAnnotatedWith(classOf[Service]))
}

/**
 * Construct a new instance of the scanner
 */
object ServiceScanner {
  def apply(config:String) = new ServiceScanner(config)
}