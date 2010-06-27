package org.brzy.interceptor

import javassist.util.proxy.MethodHandler
import java.lang.reflect.Method

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class Invoker(val factories: List[ManagedThreadContext]) extends MethodHandler {
  override def invoke(self: AnyRef, m1: Method, m2: Method, args: Array[AnyRef]): AnyRef = {

    def traverse(it: Iterator[ManagedThreadContext]): AnyRef = {
      val managedFactory = it.next
      var returnValue: AnyRef = null
      var nested = false
      val ctx =
      if (managedFactory.context.value == managedFactory.emptyState)
        managedFactory.factory.create
      else {
        nested = true
        managedFactory.context.value
      }

      managedFactory.context.withValue(ctx) {
        if (it.hasNext)
          returnValue = traverse(it)
        else
          returnValue = m2.invoke(self, args: _*)
      }

      if (!nested) {
        managedFactory.factory.destroy(ctx)
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
