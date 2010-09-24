/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.webapp.interceptor

import javassist.util.proxy.MethodHandler
import java.lang.reflect.Method
import org.brzy.fab.interceptor.ManagedThreadContext

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

      if (managedFactory.isManaged(self)) {
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
      }
      else {
        if (it.hasNext)
          returnValue = traverse(it)
        else
          returnValue = m2.invoke(self, args: _*)

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
