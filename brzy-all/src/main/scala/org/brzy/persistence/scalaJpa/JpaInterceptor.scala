package org.brzy.persistence.scalaJpa

import javax.persistence.EntityManagerFactory
import org.brzy.interceptor.{Invocation, Interceptor, Proxy}
import org.brzy.persistence.ThreadScope._
/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class JpaInterceptor(factory:EntityManagerFactory) extends Proxy with Interceptor {

  override def invoke(invocation: Invocation): AnyRef = {
    doWith(JpaThreadContext(factory)) {
      invocation.invoke
    }
  }
}