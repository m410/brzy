package org.brzy.config.common

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait MergeConfig[T] {
  def <<(that:T):T
}