package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait ConstraintValidator {
  def isValid(value:Any, context:ConstraintValidatorContext ):Boolean
}