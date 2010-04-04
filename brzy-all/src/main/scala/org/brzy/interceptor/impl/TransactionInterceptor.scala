package org.brzy.interceptor.impl

import org.slf4j.LoggerFactory
import org.brzy.interceptor.{Interceptor, Invocation}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
trait TransactionInterceptor extends Interceptor {

  private val log = LoggerFactory.getLogger(classOf[TransactionInterceptor])
  val matchingJtaAnnotation = classOf[javax.ejb.TransactionAttribute]

  abstract override def invoke(invocation: Invocation): AnyRef =
    if (matches(matchingJtaAnnotation, invocation)) {
      log.trace("=====> TX begin")
      try {
        val result = super.invoke(invocation)
        log.trace("=====> TX commit")
        result
      }
      catch {
        case e: Exception =>
          log.error("=====> TX rollback ")
          error("Some Error Happend")
      }
    }
    else
      super.invoke(invocation)
}