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