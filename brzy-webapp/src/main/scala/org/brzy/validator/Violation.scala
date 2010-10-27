package org.brzy.validator

import javax.validation.{Path, ConstraintViolation}

/**
 * @see http://download.oracle.com/javaee/6/api/javax/validation/ConstraintViolation.html
 */
case class Violation[T<:AnyRef](
        target:T,
        rejectedValue:AnyRef,
        path:String,
        message:String)
    extends ConstraintViolation[T] {

  def getConstraintDescriptor = null

  def getLeafBean = null

  def getInvalidValue = rejectedValue

  def getPropertyPath = new Path {
    def iterator = null
    override def toString = path
  }

  def getRootBeanClass = target.getClass.asInstanceOf[Class[T]]

  def getRootBean = target

  def getMessageTemplate = "template"

  def getMessage = message
}