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

import scala.actors.{Actor, Exit, TIMEOUT}
import scala.actors.Actor._
import java.util.Date

/**
 * Schedules the cron execution.  This will case an action to pause execution until the
 * react time has been reached.
 *
 * @author Michael Fortin
 */
object Scheduler {

  def interval(actOn:Actor, time: Long) = new AnyRef {
    private val executor = actor {
      loop {
        reactWithin(time) {
          case TIMEOUT => actOn!Execute
          case Exit => exit()
        }
      }
    }

    def stop()  {
      executor!Exit
      actOn!Exit
    }
  }

  def cron(actOn:Actor, pattern: String)= new AnyRef {
    private val expression = new CronExpression(pattern)
    private val executor = actor {
      loop {
        reactWithin(nextExecution(pattern)) {
          case TIMEOUT => actOn!Execute
          case Exit => exit()
        }
      }
    }

    def stop() {
      executor!Exit
      actOn!Exit
    }

    def nextExecution(pattern:String):Long = {
      expression.getNextValidTimeAfter(new Date()).getTime - System.currentTimeMillis
    }
  }
}