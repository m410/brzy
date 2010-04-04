package org.brzy.interceptor.impl

import org.slf4j.LoggerFactory
import org.brzy.interceptor.{Interceptor, Invocation}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
trait LoggingInterceptor extends Interceptor {

  private val log = LoggerFactory.getLogger(classOf[LoggingInterceptor])
  val loggingPointcut = parser.parsePointcutExpression("execution(public * *(..))")

  abstract override def invoke(invocation: Invocation): AnyRef =
    if (matches(loggingPointcut , invocation)) {
      log.debug("=====> Enter: {} @ {}" , invocation.method.getName ,invocation.target.getClass.getName)
      val result = super.invoke(invocation)
      log.debug("=====> Exit: {} @ {}", invocation.method.getName, invocation.target.getClass.getName)
      result
    }
    else
      super.invoke(invocation)
}