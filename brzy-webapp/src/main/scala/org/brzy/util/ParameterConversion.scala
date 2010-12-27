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
package org.brzy.util

/**
 * This is a helper to convert strings to other data types.  Create it's meant to be an implicit
 * value and used actions.
 * 
 * @author Michael Fortin
 */
object ParameterConversion {
  private val StringClass = classOf[java.lang.String]
  private val IntegerClass = classOf[java.lang.Integer]
  private val LongClass = classOf[java.lang.Long]
  private val LongPrimitiveClass = java.lang.Long.TYPE

  def toType(clazz:Class[_], paramVal:String):AnyRef = clazz match {
    case StringClass => paramVal
    case LongClass => java.lang.Long.valueOf(paramVal)
    case LongPrimitiveClass => java.lang.Long.valueOf(paramVal)
    case IntegerClass => java.lang.Integer.valueOf(paramVal)
    case _ => error("Unknown Type: " + clazz)
  }
}