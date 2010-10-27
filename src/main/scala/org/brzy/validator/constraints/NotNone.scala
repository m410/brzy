package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Checks the option field to see if it's empty and if it's not empty checks to see if it's null.
 * 
 * @author Michael Fortin
 */
case class NotNone(
      message:String = "{org.brzy.validator.constraints.NotNone.message}"
    ) extends ConstraintValidator {
  
  def isValid(field: AnyRef) =  field != null
}