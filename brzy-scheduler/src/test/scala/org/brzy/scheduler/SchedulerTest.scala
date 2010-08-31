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

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.util.Calendar


class SchedulerTest extends JUnitSuite {
  @Test def testSchedule = {
    val config = new SchedulerModConfig(Map(
      "name" -> "Scheduler",
      "scan_package" -> "org.brzy.scheduler.mock"
      ))
    val provider = new SchedulerModProvider(config)
    assertNotNull(provider.serviceMap)
    assertEquals(1,provider.serviceMap.size)

    provider.startup
    Thread.sleep(10000)
    provider.shutdown
  }
}