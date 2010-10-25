package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Pattern(pattern:String) extends ConstraintValidator {
  def isValid(field: AnyRef, context: ConstraintValidatorContext) =
      pattern.r.findFirstMatchIn(field.asInstanceOf[String]) match {
        case Some(str) => true
        case _ => false
      }

}