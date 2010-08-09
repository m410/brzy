package org.brzy.scheduler

import scala.actors.{Actor, Exit, TIMEOUT}
import java.util.Calendar

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
object Scheduler {
  import Actor._

  def interval(actOn:Actor, time: Long) = new AnyRef {
    private val executor = actor {
      loop {
        reactWithin(time) {
          case TIMEOUT => actOn!Execute
          case Exit => exit
        }
      }
    }

    def stop = executor!Exit
  }

  def cron(actOn:Actor, pattern: String)= new AnyRef {
    private val executor = actor {
      loop {
        reactWithin(nextExecution(pattern)) {
          case TIMEOUT => actOn!Execute
          case Exit => exit
        }
      }
    }

    def stop =  executor!Exit
  }

  /**
   * @see http://en.wikipedia.org/wiki/CRON_expression
   */
  def nextExecution(pattern:String):Long = {
    val time = System.currentTimeMillis
    val now = Calendar.getInstance
    val calendar = Calendar.getInstance
    calendar.set(Calendar.MILLISECOND,0)
    calendar.add(Calendar.SECOND,nextSecond(now,pattern))
    calendar.add(Calendar.HOUR,nextHour(now,pattern))
    calendar.add(Calendar.DAY_OF_MONTH,nextDayOfMonth(now,pattern))
    calendar.add(Calendar.MONTH,nextMonth(now,pattern))
    calendar.add(Calendar.DAY_OF_WEEK,nextDayOfWeek(now,pattern))
    calendar.add(Calendar.YEAR,nextYear(now,pattern))
    calendar.getTimeInMillis - time
  }

  protected[scheduler] val Asterisk = """^(*)$""".r
  protected[scheduler] val Digit = """^(\d+)$""".r
  protected[scheduler] val Div = """^(\d+)/(\d+)$""".r
  protected[scheduler] val Multiple = """^(\d+,)$""".r
  protected[scheduler] val Range = """^(\d+)-(\d+)$""".r

  protected[scheduler] def nextSecond(now:Calendar,pattern:String):Int = {
    val nowSecond = now.get(Calendar.SECOND)

    pattern.split(" ")(0) match {
      case Asterisk(a) => 1
      case Digit(a) =>
        val sec = Integer.parseInt(a)
        if(nowSecond <= sec)
           sec - nowSecond
        else
          sec + 60 - nowSecond
      case Div(a,b) => 
      case Multiple(a) => 1
      case Range(a,b) =>
        val start = Integer.parseInt(a)
        val stop = Integer.parseInt(a)
        if(nowSecond > start && nowSecond < stop)
          1
        elese if(nowSecond <= start)
           start - nowSecond
        else
          start + 60 - nowSecond
      case _=> error("Seconds conld not be determined for pattern: " + pattern)
    }
  }

  protected[scheduler] def nextHour(now:Calendar,pattern:String):Int = 0

  protected[scheduler] def nextDayOfMonth(now:Calendar,pattern:String):Int = 0

  protected[scheduler] def nextMonth(now:Calendar,pattern:String):Int = 0

  protected[scheduler] def nextDayOfWeek(now:Calendar,pattern:String):Int = 0

  protected[scheduler] def nextYear(now:Calendar,pattern:String):Int = 0
}