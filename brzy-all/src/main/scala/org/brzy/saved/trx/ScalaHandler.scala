package org.brzy.trx

import javassist.util.proxy.MethodHandler
import java.lang.reflect.Method

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ScalaHandler extends MethodHandler {
  def invoke(self: AnyRef, m: Method, proceed: Method, args: Array[AnyRef]) = {
    println("intercept: " + m.getName)
    proceed.invoke(self, args)
  }
}