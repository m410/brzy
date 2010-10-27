package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Future(
    message:String = "{org.brzy.validator.constraints.Future.message}"
  ) extends ConstraintValidator {

  def isValid(field: AnyRef) =  field != null
  
}