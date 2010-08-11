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