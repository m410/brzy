package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext
import org.brzy.validator.MessageInterpolator
import java.util.Locale


/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Size(
    range:Range,
    message:String = "{org.brzy.validator.constraints.Size.message}"
    ) extends ConstraintValidator {
  private[this] val StringClass = classOf[String]
  private[this] val NumberClass = classOf[Number]

  def isValid(field: AnyRef) =  field.getClass match {
    case StringClass => range.contains(field.asInstanceOf[String].length)
    case _ => false
  }

  private[this] val map = Map("range"-> {range.min + " - " + range.max})
  override def interpolate(locale: Locale) =  MessageInterpolator.interpolate(message,locale,map)
}