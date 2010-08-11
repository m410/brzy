package org.brzy.scheduler.mock

import org.brzy.scheduler.Cron
import java.util.Date

@Cron("0/5 * * * * ?")
class MockSchedulerService {
  def execute = {
    val date: Date = new Date()
    println("#### Execute Called at: " + date +" ("+ date.getTime +")")
  }
}