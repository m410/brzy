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
package org.brzy.mod.scheduler.mock

import java.util.Date
import org.brzy.service.Service
import org.brzy.mod.scheduler.{Cron, CronOld}

class MockSchedulerService extends Service with Cron {
  override val expression = "0/5 * * * * ?"

  def execute = {
    val date: Date = new Date()
    println("#### Execute Called at: " + date +" ("+ date.getTime +")")
  }
}