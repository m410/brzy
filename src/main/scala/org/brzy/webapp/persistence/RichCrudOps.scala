package org.brzy.webapp.persistence

import javax.validation.ConstraintViolation

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait RichCrudOps[T] {

  def validate:Option[Set[ConstraintViolation[AnyRef]]]

  def delete()

  def insert(commit:Boolean = false):T

  def update(commit:Boolean = false):T

  def commit()
}
