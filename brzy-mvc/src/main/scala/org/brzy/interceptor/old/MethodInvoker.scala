package org.brzy.interceptor.old

import java.lang.reflect.Method
import javassist.util.proxy.MethodHandler

/**
 * @author Michael Fortin
 */
class MethodInvoker extends MethodHandler with Invoker {
  def invoke(self: AnyRef, m1: Method, m2: Method, args: Array[AnyRef]): AnyRef =
      invoke(Invocation(m2, args, self))
}