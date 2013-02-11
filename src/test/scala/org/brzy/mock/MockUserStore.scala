package org.brzy.mock

import org.brzy.webapp.persistence.Dao
import org.brzy.validator.Validator
import scala.language.implicitConversions


trait MockUserStore extends Dao[MockUser,Long]{

  class EntityCrudOps(t:MockUser) extends PersistentCrudOps(t) {
    def validate = Validator(t).violations
    def insert(commit:Boolean  = false) = t
    def commit() {}
    def update(commit:Boolean  = false) = t
    def delete() {}
  }

  def findBy(id: Long)(implicit pk: Manifest[Long], t: Manifest[MockUser]) = new MockUser

  def getBy(id: Long)(implicit pk: Manifest[Long], t: Manifest[MockUser]) = Option(new MockUser)

  def getOrElse(id: Long, alternate: MockUser)(implicit pk: Manifest[Long], t: Manifest[MockUser]) = new MockUser

  def load(id: String)(implicit pk: Manifest[Long], t: Manifest[MockUser]) = new MockUser

  def list(size: Int, offset: Int)(implicit t: Manifest[MockUser]) = List(new MockUser)

  def count(implicit t: Manifest[MockUser]) = 1

  implicit def applyCrudOps(t: MockUser)(implicit m: Manifest[MockUser]) = new EntityCrudOps(t)
}