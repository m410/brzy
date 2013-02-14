package org.brzy.webapp.persistence

import Propagation._
import Isolation._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case object NoTransaction extends Transaction {
  def isolation = NONE
  def readOnly = true

  override def doWith(it: List[SessionFactory], scope: () => Unit) {
    scope()
  }
}
