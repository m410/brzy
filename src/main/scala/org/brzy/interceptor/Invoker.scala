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
package org.brzy.interceptor

import javassist.util.proxy.MethodHandler
import java.lang.reflect.{InvocationTargetException, Method}
import org.brzy.fab.interceptor.ManagedThreadContext

/**
 *  This is the javassist implementation of the methodHandler.  It's called on any controller
 * that in the project package space.  It takes a list of threadLocal managers and calls each
 * recursively on each call.
 *
 * @author Michael Fortin
 */
@deprecated("Not using aop any more")
class Invoker(val factories: List[ManagedThreadContext]) extends MethodHandler {

  /**
   * Execute a function inside a session context.
   */
  def doIn(body: () => AnyRef) {
    val iterator = factories.iterator

    if (iterator.hasNext)
      traverse(iterator, None)(body)
    else
      body()
  }

  override def invoke(itSelf: AnyRef, m1: Method, m2: Method, args: Array[AnyRef]): AnyRef = {
    val iterator = factories.iterator
    val fun = {() =>
      try {
        m2.invoke(itSelf, args: _*)
      }
      catch {
        case i: InvocationTargetException => throw i.getCause
      }
    }

    if (iterator.hasNext)
      traverse(iterator, Option(itSelf))(fun)
    else
      fun()
  }

  /**
   * Recursive method to call each ThreadLocal session context.
   */
  protected def traverse(it: Iterator[ManagedThreadContext], itSelf: Option[AnyRef])( target:() => AnyRef): AnyRef = {
    val managedFactory = it.next()
    var returnValue: AnyRef = null
    var nested = false

    if (itSelf.isEmpty || managedFactory.isManaged(itSelf.get)) {
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
            returnValue = traverse(it, itSelf)(target)
          else
            returnValue = target()
        }
      }
      finally {
        if (!nested)
          managedFactory.destroySession(ctx)
      }
    }
    else {
      if (it.hasNext)
        returnValue = traverse(it, itSelf)(target)
      else
        returnValue = target()

    }
    returnValue 
  }
}
