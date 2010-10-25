package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait ConstraintValidator {
  def isValid(value:AnyRef, context:ConstraintValidatorContext ):Boolean
}