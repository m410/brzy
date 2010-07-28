package org.brzy.mvc.interceptor

import javassist.util.proxy.MethodHandler
import java.lang.reflect.Method

/**
 *  This is the javassist implementation of the methodHandler.  It's called on any controller
 * that in the project package space.  It takes a list of threadLocal managers and calls each
 * recursively on each call.
 *
 * @author Michael Fortin
 */
class Invoker(val factories: List[ManagedThreadContext]) extends MethodHandler {
  override def invoke(self: AnyRef, m1: Method, m2: Method, args: Array[AnyRef]): AnyRef = {

    // nested recursive call on the threadContext
    def traverse(it: Iterator[ManagedThreadContext]): AnyRef = {
      val managedFactory = it.next

      var returnValue: AnyRef = null
      var nested = false

      val ctx =
          if (managedFactory.context.value == managedFactory.empty)
            managedFactory.createSession
          else {
            nested = true
            managedFactory.context.value
          }

      try {
        managedFactory.context.withValue(ctx) {
          if (it.hasNext)
            returnValue = traverse(it)
          else
            returnValue = m2.invoke(self, args: _*)
        }
      }
      finally {
        if (!nested) {
          managedFactory.destroySession(ctx)
        }
      }
      returnValue
    }

    val iterator = factories.iterator

    if (iterator.hasNext)
      traverse(iterator)
    else
      m2.invoke(self, args: _*)
  }

}
