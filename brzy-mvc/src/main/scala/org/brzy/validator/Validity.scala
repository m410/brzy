package org.brzy.validator

import javax.validation.ConstraintViolation
import java.util.Set
import collection.JavaConversions.JSetWrapper

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Validity[T](val violations:Array[ConstraintViolation[T]]) {

  def this() = this(Array[ConstraintViolation[T]]())
  
  def isValid:Boolean = violations.length <= 0
  
}

object Validity {
  def apply[T](constraints:Set[ConstraintViolation[T]]) = {
    new Validity[T](new JSetWrapper(constraints).toArray[ConstraintViolation[T]])
  }
}