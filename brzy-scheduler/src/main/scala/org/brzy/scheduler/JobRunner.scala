package org.brzy.scheduler

import actors.{Exit, Actor}
import org.slf4j.LoggerFactory
import org.brzy.mvc.interceptor.Invoker

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class JobRunner(service: AnyRef, val invoker:Invoker) extends Actor {
  private val log = LoggerFactory.getLogger(classOf[JobRunner])
  val cron = service.getClass.getAnnotation(classOf[Cron])
  val method = service.getClass.getMethod(cron.method)

  def act() = loop {
    react {
        case Execute =>
          log.trace("execute: {} on {}",method.getName, service)
          method.invoke(service)
	    	case Exit => exit
    }
  }
}