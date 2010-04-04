package org.brzy.saved.intercept

import java.lang.reflect.{InvocationHandler, Method}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ManagedComponentProxy(val target: AnyRef) extends InvocationHandler with InterceptHandler {

  def invoke(proxy: AnyRef, m: Method, args: Array[AnyRef]): AnyRef = invoke(Invocation(m, args, target))
}