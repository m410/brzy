package org.brzy.validator.constraints

import javax.validation.{Payload, ConstraintValidatorContext}
import java.util.Locale
import org.brzy.validator.MessageInterpolator

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait ConstraintValidator {

  def isValid(value:AnyRef):Boolean

  val message:String

  def interpolate(locale:Locale):String = MessageInterpolator.interpolate(message,locale)
}