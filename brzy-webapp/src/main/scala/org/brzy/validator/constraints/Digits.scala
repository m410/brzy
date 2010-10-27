package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext
import java.util.Locale
import org.brzy.validator.MessageInterpolator

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Digits(
    integer:Int,
    fraction:Int,
    message:String = "{org.brzy.validator.constraints.NotNull.message}"
  ) extends ConstraintValidator {

  def isValid(field: AnyRef) =  field != null

  private[this] val map = Map("integer"->integer.asInstanceOf[AnyRef],"fraction"->fraction.asInstanceOf[AnyRef])
  override def interpolate(locale: Locale) =  MessageInterpolator.interpolate(message,locale,map)

}