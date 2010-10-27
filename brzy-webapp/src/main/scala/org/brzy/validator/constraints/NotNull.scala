package org.brzy.validator.constraints

import javax.validation.{Payload, ConstraintValidatorContext}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class NotNull(
        message:String = "{org.brzy.validator.constraints.NotNull.message}"
    ) extends ConstraintValidator {
  
  def isValid(field: AnyRef) =  field != null
}