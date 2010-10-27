package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Past(
        message:String = "{org.brzy.validator.constraints.Past.message}"
    ) extends ConstraintValidator {
  def isValid(field: AnyRef) =  field != null
}