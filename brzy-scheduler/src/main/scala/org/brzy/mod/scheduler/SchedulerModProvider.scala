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
package org.brzy.mod.scheduler

import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.Reflections
import collection.JavaConversions._
import collection.mutable.{ListBuffer, HashMap}
import org.brzy.fab.mod.ModProvider

/**
 * Cron scheduler service provider.
 *
 * @author Michael Fortin
 */
class SchedulerModProvider(c: SchedulerModConfig) extends ModProvider {
  val name = c.name.get

    private[this] val reflections = new Reflections(new ConfigurationBuilder()
      .setUrls(ClasspathHelper.getUrlsForPackagePrefix(c.scanPackage.get))
      .setScanners(new SubTypesScanner()))

  val services =
      reflections.getSubTypesOf(classOf[Cron]).toList.filter(_.getName.indexOf("$") < 0)

  val jobs = {
    val list = ListBuffer[Schedule]()
    services.foreach(s=> {
      val instance = s.newInstance.asInstanceOf[Cron]
      list += Schedule(new JobRunner(instance),instance.expression)
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