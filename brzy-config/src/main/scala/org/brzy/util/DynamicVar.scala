package org.brzy.util

/**
 * 
 * @author Michael Fortin
 */
class DynamicVar[T] {
  private val threadLocal = new ThreadLocal[T]
  def get: T = threadLocal.get

  def doWith[R](x: T)(func : => R) : R = {
    val original = get
    try {
      threadLocal.set(x)
      func
    } finally {
      threadLocal.set(original)
    }
  }
}