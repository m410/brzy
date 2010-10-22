package org.brzy.validator

import constraints.ConstraintValidator
import javax.validation.{ConstraintValidatorContext, ConstraintViolation}
import collection.immutable.Set

/**
 * 
 * @see http://download.oracle.com/javaee/6/tutorial/doc/gircz.html
 * @author Michael Fortin
 */
class Validator[T<:AnyRef](target:T,
    context:ConstraintValidatorContext = new DefaultConstraintValidatorContext,
    val violations:Option[Set[ConstraintViolation[T]]] = None) {

  def check(field:String, checks:ConstraintValidator*):Validator[T] = {
    val errors = checks.filter(constraint=>{
      val value = target.getClass.getMethod(field).invoke(target)
      !constraint.isValid(value, context)
    }).map(constraint=>{
      val value = target.getClass.getMethod(field).invoke(target)
      new Violation[T](target,value,"message")
    })

    if(errors.isEmpty)
      new Validator(target,context, violations)
    else {
      val others = violations.getOrElse(Set.empty[ConstraintViolation[T]])
      new Validator(target,context, Option(errors.toSet ++ others))
    }
  }
}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object Validator {
  def apply(target:AnyRef) = {
    new Validator(target)
  }
}
