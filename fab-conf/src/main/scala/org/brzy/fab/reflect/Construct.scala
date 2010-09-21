/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.fab.reflect

import java.beans.ConstructorProperties

/**
 * Helper class that constructs instances of scala classes using the ConstructorProperties
 * annotation java bean annotation.
 *
 * @see java.beans.ConstuctorProperties
 * @author Michael Fortin
 */
object Construct {

  private[this] val StringCls = classOf[String]
  private[this] val IntCls = classOf[Int]
  private[this] val LongCls = classOf[Long]
  private[this] val FloatCls = classOf[Float]
  private[this] val DoubleCls = classOf[Double]
  private[this] val BooleanCls = classOf[Boolean]

  /**
   * If there is more than one constructor this looks for the one with the most
   * arguments and uses it.  This uses the Name annotation to identify the
   * constructor args.
   */
  def apply[T](map: Map[String, Any])(implicit m: Manifest[T]): T = {
    val c = m.erasure
    assert(c.getAnnotation(classOf[ConstructorProperties]) != null, "Missing ConstructorProperties Annotation")

    val argNames = c.getAnnotation(classOf[ConstructorProperties]).value.asInstanceOf[Array[String]]
    val constructor = c.getConstructors.find(c => c.getParameterTypes.size == map.size).get
    val args = argNames.map(name => map.get(name).get).asInstanceOf[Array[_ <: Object]]
    constructor.newInstance(args: _*).asInstanceOf[T]
  }

  /**
   * Checks the type of the constructor argument and tries to cast the input string to
   * the right type.
   */
  def withCast[T](map: Map[String, String])(implicit m: Manifest[T]): T = {
    val c = m.erasure
    assert(c.getAnnotation(classOf[ConstructorProperties]) != null, "Missing ConstructorProperties Annotation")

    val argNames = c.getAnnotation(classOf[ConstructorProperties]).value.asInstanceOf[Array[String]]
    // TODO this is wrong, what if there are more parameters submitted than used in the constructor
    val constructor = c.getConstructors.find(c => c.getParameterTypes.size == map.size).get

    val args = argNames.map(name => {
      val argType = constructor.getParameterTypes()(argNames.indexOf(name))
      argType match {
        case StringCls => map(name)
        case BooleanCls => map(name).toBoolean
        case IntCls => map(name).toInt
        case LongCls => map(name).toLong
        case DoubleCls => map(name).toDouble
        case FloatCls => map(name).toFloat
        case _ => null // TODO need to handle dates and other objects somehow
      }
    }).asInstanceOf[Array[_ <: Object]]
    constructor.newInstance(args:_*).asInstanceOf[T]
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

  def apply[T](name:String, args: Array[_<:java.lang.Object]): T = {
    val c = Class.forName(name)
    val constructor = c.getConstructor(args.map(_.getClass):_*)
    constructor.newInstance(args:_*).asInstanceOf[T]
  }
}