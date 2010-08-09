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
  val execute = service.execute _

  def act() = loop {
    react {
        case Execute =>
          log.print("execute: {}" + service)
          execute.apply
	    	case Exit => exit
    }
  }
}