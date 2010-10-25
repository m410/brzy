package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Digits(integer:Int, fraction:Int) extends ConstraintValidator {
  def isValid(field: AnyRef, context: ConstraintValidatorContext) =  field != null
}