package org.brzy.webapp.persistence

import Propagation._
import Isolation._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class DefaultTransaction(
        propagation: Propagation = REQUIRED,
        isolation: Isolation = DEFAULT,
        readOnly:Boolean = false)
        extends Transaction
