package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case object NotNull extends ConstraintValidator {
  def isValid(field: Any, context: ConstraintValidatorContext) =  field != null
}