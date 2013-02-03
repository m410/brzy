package org.brzy.persistence

import org.brzy.fab.interceptor.ManagedThreadContext
import Propagation._
import Isolation._

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
case class Transaction(propagation: Propagation = REQUIRED, isolation: Isolation = Default) {


  def doWith(t: List[ManagedThreadContext], scope: () => Unit) {
    //before
    scope()
    //after
  }

  def doWithResult(t: List[ManagedThreadContext], scope: () => AnyRef) {
    //before
    val anyRef = scope()
    //after
    anyRef
  }
}
