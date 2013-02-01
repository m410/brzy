package org.brzy.exp

import org.brzy.fab.interceptor.ManagedThreadContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class Transaction {

  def doIn(t:List[ManagedThreadContext],scope:()=>Unit) {
    //before
    scope()
    //after
  }
}
