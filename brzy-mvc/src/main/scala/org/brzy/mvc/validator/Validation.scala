package org.brzy.mvc.validator

import javax.validation.ConstraintViolation
import java.util.Set
import collection.JavaConversions.JSetWrapper

/**
 * @author Michael Fortin
 */
class Validation[T](val violations:Array[ConstraintViolation[T]]) {

  def this() = this(Array[ConstraintViolation[T]]())
  
  def passes:Boolean = violations.length <= 0
  
}

object Validation {
  def apply[T](constraints:Set[ConstraintViolation[T]]) = {
    new Validation[T](new JSetWrapper(constraints).toArray[ConstraintViolation[T]])
  }
}