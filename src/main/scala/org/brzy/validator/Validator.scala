package org.brzy.validator

import constraints.ConstraintValidator
import javax.validation.{ConstraintValidatorContext, ConstraintViolation}
import collection.immutable.Set
import java.util.Locale

/**
 * 
 * @see http://download.oracle.com/javaee/6/tutorial/doc/gircz.html
 * @author Michael Fortin
 */
class Validator[T<:AnyRef](target:T,
    val violations:Option[Set[ConstraintViolation[T]]] = None,
    locale:Locale = Locale.getDefault) {

  def check(field:String, checks:ConstraintValidator*):Validator[T] = {
    val errors = checks.filter(constraint=>{
      val value = target.getClass.getMethod(field).invoke(target)
      !constraint.isValid(value)
    }).map(constraint=>{
      val value = target.getClass.getMethod(field).invoke(target)
      Violation[T](target,value,field,constraint.interpolate(locale))
    })

    if(errors.isEmpty)
      new Validator(target, violations)
    else {
      val others = violations.getOrElse(Set.empty[ConstraintViolation[T]])
      new Validator(target, Option(errors.toSet ++ others))
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
