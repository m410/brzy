package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Min(min:Long) extends ConstraintValidator {
  def isValid(field: AnyRef, context: ConstraintValidatorContext) =  field != null
}