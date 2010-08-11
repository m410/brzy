package org.brzy.scheduler

import scala.actors.{Actor, Exit, TIMEOUT}
import scala.actors.Actor._
import java.util.{Date, Calendar}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
object Scheduler {

  def interval(actOn:Actor, time: Long) = new AnyRef {
    private val executor = actor {
      loop {
        reactWithin(time) {
          case TIMEOUT => actOn!Execute
          case Exit => exit
        }
      }
    }

    def stop =  {
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
          case Exit => exit
        }
      }
    }

    def stop:Unit =  {
      executor!Exit
      actOn!Exit
    }

    def nextExecution(pattern:String):Long = {
      expression.getNextValidTimeAfter(new Date()).getTime - System.currentTimeMillis
    }
  }
}