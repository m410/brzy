package org.brzy.mock

import org.brzy.persistence.Dao
import org.brzy.validator.Validator
import scala.language.implicitConversions


trait MockUserStore extends Dao[MockUser,Long]{

  /**
   * Retrieve the object by it's primary key
   */
  def findBy(id: Long) = null

  /**
   * Retrieve a single entity by primary key.
   */
  def getBy(id: Long) = None

  class EntityCrudOps(t:MockUser) extends PersistentCrudOps(t) {
    def validate = Validator(t).violations
    def insert(commit:Boolean  = false) = t
    def commit() {}
    def update() = new MockUser()
    def delete() {}
  }

  def newPersistentCrudOps(t: MockUser) = new EntityCrudOps(t)
  implicit def applyCrudOps(t:MockUser) = new EntityCrudOps(t)
  def apply(id:Long) = new MockUser()
  def get(id:Long) = Option(new MockUser())
  def getOrElse(id: Long, alternate: MockUser) = alternate
  def count:Long = 1
  def list = List()
  def list(start:Int, size:Int) = List()
  def load(id:String) = new MockUser()

}