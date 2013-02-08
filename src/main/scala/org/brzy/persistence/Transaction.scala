package org.brzy.persistence

import org.brzy.fab.interceptor.ThreadContextSessionFactory
import Propagation._
import Isolation._

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Transaction(propagation: Propagation = REQUIRED, isolation: Isolation = Default, readOnly:Boolean = false) {


  def doWith(it: List[ThreadContextSessionFactory], scope: () => Unit) {
    val iterator = it.iterator

    if (iterator.hasNext)
      traverse(iterator, scala.None)(scope)
    else
      scope() // no managed thread contexts
  }


  /**
   * Recursive method to call each ThreadLocal session context.
   */
  protected def traverse(it: Iterator[ThreadContextSessionFactory], itSelf: Option[AnyRef])( target:() => Unit) {
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
