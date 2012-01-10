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

import actors.{Exit, Actor}
import org.slf4j.LoggerFactory
import org.brzy.application.WebApp

/**
 * The scala Actor that runs the Cron job for a single cron service.
 *
 * @author Michael Fortin
 */
class JobRunner(val service: Cron, app:Option[WebApp]) extends Actor {
  private val log = LoggerFactory.getLogger(classOf[JobRunner])

  val serviceName = {
    val className = service.getClass.getSimpleName
    className.charAt(0).toLower + className.substring(1, className.length)
  }

  def act() {
    loop {
      react {
        case Execute =>
          app match {
            case Some(a) =>
              try {
                a.interceptor.doIn(() => {
                  log.debug("execute with interceptor: {}", service)
                  service.execute()
                  None
                })
              }
              catch {
                case t:Throwable => log.error(t.getMessage,t)
              }
            case _ =>
              try {
                log.debug("execute without interceptor: {}", service)
                service.execute()
              }
              catch {
                case t:Throwable => log.error(t.getMessage,t)
              }
          }
        case Exit =>
          exit()
      }
    }
  }
}