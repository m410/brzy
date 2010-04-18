package org.brzy.config

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

trait Merge[T] {
  def +(that:T):T
}