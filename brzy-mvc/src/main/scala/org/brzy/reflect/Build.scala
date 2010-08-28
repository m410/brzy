package org.brzy.reflect

import collection.mutable.ArrayBuffer
import java.beans.ConstructorProperties

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object Build {

  def apply[T]()(implicit m: Manifest[T]) = new Builder[T]()

  class Builder[T]()(implicit m: Manifest[T]) {
    private[this] val cls = m.erasure
    private[this] val argNames =  cls.getAnnotation(classOf[ConstructorProperties]).value
    private[this] val constructor =
        cls.getConstructors.find(c=> c.getParameterTypes.length == argNames.length) match {
          case Some(a) => a
          case _ => error("No matching Constructor found for ConstructorProperties annotation.")
        }

    private[this] val args = new ArrayBuffer[java.lang.Object](constructor.getParameterTypes.length)

    def arg(a:(String, java.lang.Object)) = {
      args.insert(argNames.indexOf(a._1),a._2)
      this
    }

    def make:T = constructor.newInstance(args.toArray:_*).asInstanceOf[T]
  }
}