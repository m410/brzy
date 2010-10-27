package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext
import java.util.Locale
import org.brzy.validator.MessageInterpolator

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Max(
        max:Long,
        message:String = "{org.brzy.validator.constraints.Future.message}")
    extends ConstraintValidator {
  
  def isValid(field: AnyRef) =  field != null

  private[this] val map = Map("max"->max.asInstanceOf[AnyRef])
  override def interpolate(locale: Locale) =  MessageInterpolator.interpolate(message,locale,map)
}