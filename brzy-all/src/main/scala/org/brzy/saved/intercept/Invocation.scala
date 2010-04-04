package org.brzy.saved.intercept

import java.lang.reflect.Method

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
case class Invocation(val method: Method, val args: Array[AnyRef], val target: AnyRef) {

  def invoke: AnyRef = method.invoke(target, args:_*)

  override def toString: String = "Invocation [method: " + method.getName +
          ", args: " + args + ", target: " + target + "]"
}