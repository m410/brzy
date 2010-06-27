package org.brzy.interceptor

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
trait ContextFactory[T] {
  def create: T
  def destroy(s: T)
}