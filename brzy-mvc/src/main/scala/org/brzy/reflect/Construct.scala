package org.brzy.reflect

import java.beans.ConstructorProperties

/**
 * Helper class that constructs instances of scala classes using the ConstructorProperties
 * annotation java bean annotation.
 *
 * @see java.beans.ConstuctorProperties
 * @author Michael Fortin
 */
object Construct {

  /**
   * If there is more than one constructor this looks for the one with the most
   * arguments and uses it.  This uses the Name annotation to identify the
   * constructor args.
   */
  def apply[T](map: Map[String, Any])(implicit m: Manifest[T]): T = {
    val c = m.erasure
    val argNames = c.getAnnotation(classOf[ConstructorProperties]).value.asInstanceOf[Array[String]]
    val constructor = c.getConstructors.find(c => c.getParameterTypes.size == map.size).get
    val args = argNames.map(name => map.get(name).get).asInstanceOf[Array[_ <: Object]]
    constructor.newInstance(args: _*).asInstanceOf[T]
  }

  /**
   * Creates a class using the constructor with the same number of arguments.
   */
  def apply[T](a: Array[_<:java.lang.Object])(implicit m: Manifest[T]): T = {
    val c = m.erasure
    val constructor = c.getConstructors.find(c => c.getParameterTypes.size == a.size).get
    constructor.newInstance(a: _*).asInstanceOf[T]
  }

  /**
   * Creates a class using the no arguments constructor.
   */
  def apply[T]()(implicit m: Manifest[T]): T = {
    m.erasure.getConstructor(Array[Class[_]]():_*).newInstance(Array[java.lang.Object]():_*).asInstanceOf[T]
  }

  def apply[T](n:String, a: Array[_<:java.lang.Object]): T = {
    val c = Class.forName(n)
    val constructor = c.getConstructor(a.map(_.getClass):_*)
    constructor.newInstance(a: _*).asInstanceOf[T]
  }
}