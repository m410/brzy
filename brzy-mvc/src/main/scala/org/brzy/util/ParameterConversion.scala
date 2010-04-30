package org.brzy.util

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
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