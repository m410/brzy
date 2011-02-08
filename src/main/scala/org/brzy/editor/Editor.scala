package org.brzy.editor

import java.util.Date
import java.text.{ParsePosition, DecimalFormat, SimpleDateFormat}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
trait Editor[T] {
  def isConstructor: Boolean
  def property: String
  def index: Int
  def editorClass: Class[_]
  def fromText(txt: String): T

  def toText(t: T): String = {
    t match {
      case x:AnyRef =>
        if(x == null)
        null//.asInstanceOf[T]
      else
        x.toString
      case _ => error("Only AnyRef is supported")
    }
  }
}

/**
 *
 */
abstract class PropertyEditor[T:Manifest] extends Editor[T] {
  val m = manifest[T]
  def property: String
  def index: Int = -1
  def isConstructor = index >= 0
  val editorClass = m.erasure
}

/**
 *
 */
case class StringEditor(property: String, override val index: Int = -1) extends PropertyEditor[String] {
  def fromText(txt: String) = txt
}

/**
 *
 */
case class BooleanEditor(property: String, override val index: Int = -1) extends PropertyEditor[Boolean] {

  def fromText(txt: String) = {
    if(txt == null)
      null.asInstanceOf[Boolean]
    else
      txt.toBoolean
  }
}

/**
 *
 */
case class DateEditor(property: String, format: String, override val index: Int = -1)
        extends PropertyEditor[Date] {
  val dateFormat = new SimpleDateFormat(format)

  override def toText(t: Date) = {
    if (t == null)
      null.asInstanceOf[String]
    else
      dateFormat.format(t)
  }

  def fromText(txt: String) = {
    if (txt == null)
      null.asInstanceOf[Date]
    else
      dateFormat.parse(txt)
  }
}

/**
 *
 */
case class NumberEditor[N <: AnyRef](property: String, format: String = "0", override val index: Int = -1)
        (implicit m: Manifest[N]) extends PropertyEditor[N] {
  val numberFormat  = new DecimalFormat(format)
  
  override def toText(t: N) = {
    if (t == null)
      null.asInstanceOf[String]
    else
      numberFormat.format(t).toString
  }

  def fromText(txt: String) = {
    if (txt == null)
      null.asInstanceOf[N]
    else {
      val number = numberFormat.parse(txt, new ParsePosition(0))
//      number.asInstanceOf[N]
      editorClass match {
        case ByteClass     => number.byteValue.asInstanceOf[N]
        case JByteClass    => number.byteValue.asInstanceOf[N]
        case DoubClass     => number.doubleValue.asInstanceOf[N]
        case JDoubleClass  => number.doubleValue.asInstanceOf[N]
        case FloatClass    => number.floatValue.asInstanceOf[N]
        case JFloatClass   => number.floatValue.asInstanceOf[N]
        case IntClass      => number.intValue.asInstanceOf[N]
        case IntegerClass  => number.intValue.asInstanceOf[N]
        case LongClass     => number.longValue.asInstanceOf[N]
        case JLongClass    => number.longValue.asInstanceOf[N]
        case ShortClass    => number.shortValue.asInstanceOf[N]
        case JShortClass   => number.shortValue.asInstanceOf[N]
      }
    }
  }
}