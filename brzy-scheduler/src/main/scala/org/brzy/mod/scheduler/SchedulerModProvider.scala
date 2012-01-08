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

import org.reflections.scanners.SubTypesScanner
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.Reflections
import collection.JavaConversions._
import collection.mutable.ListBuffer
import org.brzy.fab.mod.ModProvider
import org.brzy.application.WebApp
import org.slf4j.LoggerFactory

/**
 * Cron scheduler service provider.
 *
 * @author Michael Fortin
 */
class SchedulerModProvider(c: SchedulerModConfig) extends ModProvider {
  private val log = LoggerFactory.getLogger(getClass)
  val jobs = ListBuffer[Schedule]()
  val name = c.name.get

  if (c.autoDiscover) {
    val reflections = new Reflections(new ConfigurationBuilder()
            .setUrls(ClasspathHelper.getUrlsForPackagePrefix(c.scanPackage.get))
            .setScanners(new SubTypesScanner()))

    val services =
      reflections.getSubTypesOf(classOf[Cron]).toList.filter(_.getName.indexOf("$") < 0)

    services.foreach(s => {
      val instance = s.newInstance.asInstanceOf[Cron]
      jobs += Schedule(new JobRunner(instance,None), instance.expression)
    })
  }

  def addScheduledService(service: Cron, app: WebApp) {
    jobs += Schedule(new JobRunner(service, Option(app)), service.expression)
  }

  override def startup() {
    jobs.foreach(job=>{
      log.debug("start cron: {}",job)
      job.start()
    })
  }

  override def shutdown() {
    jobs.foreach(job=>{
      log.debug("stop cron: {}",job)
      job.stop()
    })
  }

}