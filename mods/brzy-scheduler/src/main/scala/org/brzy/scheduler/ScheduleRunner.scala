package org.brzy.scheduler

import actors.Actor._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class ScheduleRunner {

  def run = {
    val cron = actor {
      loop {
        // do something
      }
    }
  }
}