package org.brzy.validator.constraints

import javax.validation.ConstraintValidatorContext


/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
case class Size(range:Range) extends ConstraintValidator {
  private[this] val StringClass = classOf[String]
  private[this] val NumberClass = classOf[Number]

  def isValid(field: AnyRef, context: ConstraintValidatorContext) =  field.getClass match {
    case StringClass => range.contains(field.asInstanceOf[String].length)
    case _ => false
  }
}