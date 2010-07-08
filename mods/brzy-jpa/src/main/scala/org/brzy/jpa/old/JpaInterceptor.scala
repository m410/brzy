package org.brzy.jpa.old

import javax.persistence.EntityManagerFactory
import org.brzy.interceptor.old.{Invocation, Interceptor, Invoker}
import org.brzy.persistence.ThreadScope._
/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class JpaInterceptor(factory:EntityManagerFactory) extends Invoker with Interceptor {

  override def invoke(invocation: Invocation): AnyRef = {
    doWith(JpaThreadContext(factory)) {
      invocation.invoke
    }
  }
}