package org.brzy.validator

import javax.validation.ConstraintViolation

/**
 * @see http://download.oracle.com/javaee/6/api/javax/validation/ConstraintViolation.html
 */
class Violation[T<:AnyRef](target:T, value:AnyRef, message:String) extends ConstraintViolation[T] {

  def getConstraintDescriptor = null

  def getInvalidValue = value

  def getPropertyPath = null

  def getLeafBean = null

  def getRootBeanClass = target.getClass

  def getRootBean = target

  def getMessageTemplate = "template"

  def getMessage = message
}