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
package org.brzy.scheduler

import org.brzy.config.mod.ModProvider
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.Reflections
import collection.JavaConversions._
import collection.mutable.{ListBuffer, HashMap}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class SchedulerModProvider(c: SchedulerModConfig) extends ModProvider {
  val name = c.name.get

  private val reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.getUrlsForPackagePrefix(c.scanPackage.get))
          .setScanners(
    new ResourcesScanner(),
    new TypeAnnotationsScanner(),
    new SubTypesScanner()))

  val jobs = {
    val services = asSet(reflections.getTypesAnnotatedWith(classOf[Cron]))
    val list = ListBuffer[Schedule]()
    services.foreach(s=> {
      val instance = s.newInstance.asInstanceOf[AnyRef]
      val annotation = s.getAnnotation(classOf[Cron])
      list += Schedule(new JobRunner(instance, null),annotation.value)
    })
    list.toList
  }



  override val serviceMap = {
    val map = HashMap[String, AnyRef]()
    jobs.foreach(job=>  map += job.serviceName -> job.service )
    map.toMap
  }

  override def startup = jobs.foreach(_.start)

  override def shutdown = jobs.foreach(_.stop)

}