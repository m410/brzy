package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext
import org.brzy.validator.MessageInterpolator
import java.util.Locale

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Min(
        min:Long,
        message:String = "{org.brzy.validator.constraints.Min.message}"
    ) extends ConstraintValidator {
  def isValid(field: AnyRef) =  field != null

  private[this] val map = Map("min"->min.asInstanceOf[AnyRef])
  override def interpolate(locale: Locale) =  MessageInterpolator.interpolate(message,locale,map)
}