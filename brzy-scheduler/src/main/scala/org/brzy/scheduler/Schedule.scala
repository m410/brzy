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

import actors.{Exit, TIMEOUT, Actor}
import Actor._
import java.util.Date

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Schedule(actOn: JobRunner, pattern: String) {
  val serviceName = actOn.serviceName
  val service = actOn.service

  private val expression = new CronExpression(pattern)

  private val executor = actor {
    loop {
      reactWithin(nextExecution(pattern)) {
        case TIMEOUT => actOn ! Execute
        case Exit => exit
      }
    }
  }

  def stop: Unit = {
    executor ! Exit
    actOn ! Exit
  }

  def start: Unit = {
    actOn.start
    executor.start
  }

  def nextExecution(pattern: String): Long = {
    expression.getNextValidTimeAfter(new Date()).getTime - System.currentTimeMillis
  }
}