package org.brzy.mock

import org.brzy.webapp.persistence.{Store, RichCrudOps}
import org.brzy.validator.Validator

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait MockUserComponent {
  class MockCrudOps(m:MockUser) extends RichCrudOps[MockUser] {
    def validate = Validator(m).violations
    def insert(commit:Boolean  = false) = m
    def commit() {}
    def update(commit:Boolean  = false) = m
    def delete() {}
  }

  def userCrudOps(m:MockUser) = new MockCrudOps(m)

  class MockUserStore extends Store[Long,MockUser] {

    def apply(id: Long) = new MockUser

    def get(id: Long) = Option(new MockUser)

    def make(map:Map[String,AnyRef])(implicit m: Manifest[MockUser]) = new MockUser

    def list(size: Int = 50, offset: Int = 0) = List(new MockUser)

    def count = 1

    def blankInstance= new MockUser

  }

  val mockUserStore = new MockUserStore
}
