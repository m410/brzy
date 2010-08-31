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
package org.brzy.reflect

import collection.mutable.ArrayBuffer
import java.beans.ConstructorProperties

/**
 * This uses the builder design pattern to construct scala classes.  It requires that the
 * class has the java.bean.ConstructorProperies annotation at the class level and a matching
 * constructor.  It creats the array of constructor arguments and using reflection creates
 * an instance of the class.
 * 
 * @author Michael Fortin
 */
object Build {

  def apply[T]()(implicit m: Manifest[T]) = new Builder[T]()

  /**
   * The Builer implemetation
   */
  class Builder[T]()(implicit m: Manifest[T]) {
    private[this] val cls = m.erasure
    private[this] val argNames =  cls.getAnnotation(classOf[ConstructorProperties]).value
    private[this] val constructor =
        cls.getConstructors.find(c=> c.getParameterTypes.length == argNames.length) match {
          case Some(a) => a
          case _ => error("No matching Constructor found for ConstructorProperties annotation.")
        }

    private[this] val args = new ArrayBuffer[java.lang.Object](constructor.getParameterTypes.length)

    /**
     * Populate one of the constructor arguments.
     */
    def arg(a:(String, java.lang.Object)) = { // TODO: Change Object to Any
      args.insert(argNames.indexOf(a._1),a._2)
      this
    }

    /**
     * Create a new instance.
     */
    def make:T = constructor.newInstance(args.toArray:_*).asInstanceOf[T]
  }
}