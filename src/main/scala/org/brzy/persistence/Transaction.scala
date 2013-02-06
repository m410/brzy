package org.brzy.persistence

import org.brzy.fab.interceptor.ManagedThreadContext
import Propagation._
import Isolation._

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Transaction(propagation: Propagation = REQUIRED, isolation: Isolation = Default, readOnly:Boolean = false) {


  def doWith(it: List[ManagedThreadContext], scope: () => Unit) {
    val iterator = it.iterator

    if (iterator.hasNext)
      traverse(iterator, scala.None)(scope)
    else
      scope() // no managed thread contexts
  }

  def doWithResult(t: List[ManagedThreadContext], scope: () => AnyRef):AnyRef = {
    val iterator = t.iterator

    if (iterator.hasNext)
      traverseWithResult(iterator, scala.None)(scope)
    else
      scope() // no managed thread contexts
  }

  /**
   * Recursive method to call each ThreadLocal session context.
   */
  protected def traverseWithResult(it: Iterator[ManagedThreadContext], itSelf: Option[AnyRef])( target:() => AnyRef): AnyRef = {
    val managedFactory = it.next()
    var returnValue: AnyRef = null
    var nested = false

    if (itSelf.isEmpty || managedFactory.isManaged(itSelf.get)) {
      val ctx = {
        if (managedFactory.context.value == managedFactory.empty)
          managedFactory.createSession
        else {
          nested = true
          managedFactory.context.value
        }
      }

      try {
        managedFactory.context.withValue(ctx) {

          if (it.hasNext)
            returnValue = traverseWithResult(it, itSelf)(target)
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
        returnValue = traverseWithResult(it, itSelf)(target)
      else
        returnValue = target()
    }

    returnValue
  }

  /**
   * Recursive method to call each ThreadLocal session context.
   */
  protected def traverse(it: Iterator[ManagedThreadContext], itSelf: Option[AnyRef])( target:() => Unit) {
    val managedFactory = it.next()
    var nested = false

    if (itSelf.isEmpty || managedFactory.isManaged(itSelf.get)) {
      val ctx = {
        if (managedFactory.context.value == managedFactory.empty)
          managedFactory.createSession
        else {
          nested = true
          managedFactory.context.value
        }
      }

      try {
        managedFactory.context.withValue(ctx) {

          if (it.hasNext)
            traverse(it, itSelf)(target)
          else
            target()
        }
      }
      finally {
        if (!nested)
          managedFactory.destroySession(ctx)
      }
    }
    else {
      if (it.hasNext)
        traverse(it, itSelf)(target)
      else
        target()
    }
  }
}
