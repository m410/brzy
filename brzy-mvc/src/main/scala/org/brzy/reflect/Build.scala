package org.brzy.reflect

import collection.mutable.ArrayBuffer

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class Build[T]()(implicit m:Manifest[T]) {
  private[this] val cls = m.erasure
  private[this] val args = ArrayBuffer[Any]()

  def arg(name:String, value:Any) = {
    this
  }

  def make:T = {
    cls.newInstance.asInstanceOf[T]
  }
}