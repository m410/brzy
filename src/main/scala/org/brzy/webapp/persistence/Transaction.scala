package org.brzy.webapp.persistence

import Isolation._
import org.slf4j.LoggerFactory

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
trait Transaction {

  private val log = LoggerFactory.getLogger(getClass)

  def isolation: Isolation // = Default

  def readOnly:Boolean // = false


  def doWith(it: List[SessionFactory], scope: () => Unit) {
    log.debug("doWith session factories: {}",it)
    val iterator = it.iterator

    if (iterator.hasNext)
      traverse(iterator)(scope)
    else
      scope() // no managed thread contexts
  }


  /**
   * Recursive method to call each ThreadLocal session context.
   */
  protected def traverse(it: Iterator[SessionFactory])( target:() => Unit) {
    val managedFactory = it.next()
    var nested = false

      val ctx = {
        if (managedFactory.context.value == managedFactory.empty)
          managedFactory.createSession(isolation, readOnly)
        else {
          nested = true
          managedFactory.context.value
        }
      }

      try {
        managedFactory.context.withValue(ctx) {

          if (it.hasNext)
            traverse(it)(target)
          else
            target()
        }
      }
      finally {
        if (!nested)
          managedFactory.destroySession(ctx)
      }
  }
}
