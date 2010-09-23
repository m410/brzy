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

import actors.{Exit, Actor}
import org.slf4j.LoggerFactory
import org.brzy.webapp.interceptor.Invoker

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class JobRunner(val service: AnyRef, val invoker:Invoker) extends Actor {
  private val log = LoggerFactory.getLogger(classOf[JobRunner])

  val cron = service.getClass.getAnnotation(classOf[Cron])

  val method = service.getClass.getMethod(cron.method)

  val serviceName = {
    val className = service.getClass.getSimpleName
    className.charAt(0).toLower + className.substring(1,className.length)
  }

  def act() = loop {
    react {
        case Execute =>
          log.trace("execute: {} on {}",method.getName, service)
          method.invoke(service)
	    	case Exit => exit
    }
  }
}