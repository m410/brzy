package org.brzy.scheduler

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import java.util.Calendar


class SchedulerTest extends JUnitSuite {
  val cal = Calendar.getInstance
  cal.set(Calendar.MILLISECOND,0)
  cal.set(Calendar.SECOND,20)
  cal.set(Calendar.MINUTE,20)
  cal.getTime

  @Test def testNextSecond = {
    assertEquals(1 ,Scheduler.nextSecond(cal,"* * * * *"))
    assertEquals(40,Scheduler.nextSecond(cal,"0 * * * *"))
    assertEquals(20,Scheduler.nextSecond(cal,"40 * * * *"))
    assertEquals(20,Scheduler.nextSecond(cal,"0/15 * * * *"))
    assertEquals(41,Scheduler.nextSecond(cal,"1-15 * * * *"))
    assertEquals(5 ,Scheduler.nextSecond(cal,"45,50,56 * * * *"))
  }

  @Test def testNextMinute = {
    assertEquals(1 ,Scheduler.nextSecond(cal,"* * * * *"))
    assertEquals(40,Scheduler.nextSecond(cal,"0 0 * * *"))
    assertEquals(20,Scheduler.nextSecond(cal,"0 40 * * *"))
    assertEquals(20,Scheduler.nextSecond(cal,"0 0/15 * * *"))
    assertEquals(41,Scheduler.nextSecond(cal,"0 5-10 * * * *"))
    assertEquals(5 ,Scheduler.nextSecond(cal,"0 0,20,40 * * *"))
  }

  @Test def testNextHour= {
    assertEquals(1 ,Scheduler.nextSecond(cal,"* * * * *"))
    assertEquals(40,Scheduler.nextSecond(cal,"0 0 0 * *"))
    assertEquals(20,Scheduler.nextSecond(cal,"0 0 2 * *"))
    assertEquals(20,Scheduler.nextSecond(cal,"0 0 0/6 * *"))
    assertEquals(41,Scheduler.nextSecond(cal,"0 0 5-10 * * *"))
    assertEquals(5 ,Scheduler.nextSecond(cal,"0 0 0,4,10 * * *"))
  }

}