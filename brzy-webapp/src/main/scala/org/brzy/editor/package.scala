package org.brzy

import java.text.DecimalFormat

/**
 * This package houses an api that resembles the java beans property editors.  Note this will be
 * changed in future releases.  Instead of using reflection it will be converted
 * to use the scalabeans api.
 *
 * @author Michael Fortin
 */
package object editor {
  val ByteClass     = classOf[Byte]
  val JByteClass    = classOf[java.lang.Byte]
  val DoubClass     = classOf[Double]
  val JDoubleClass  = classOf[java.lang.Double]
  val FloatClass    = classOf[Float]
  val JFloatClass   = classOf[java.lang.Float]
  val IntClass      = classOf[Int]
  val IntegerClass  = classOf[java.lang.Integer]
  val LongClass     = classOf[Long]
  val JLongClass    = classOf[java.lang.Long]
  val ShortClass    = classOf[Short]
  val JShortClass   = classOf[java.lang.Short]
}