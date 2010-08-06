package org.brzy.persistence

import java.lang.String
import org.brzy.mvc.action.args.Parameters
import collection.immutable.Map
import javax.validation.Validation


class MockPersistable[E<:Persistent[_],PK] extends Persistable[E,PK] {

  def construct(m: Parameters) = null.asInstanceOf[E]

  def list(size: Int, offset: Int) = Nil

  def list() = Nil

  def load(id: String) = null.asInstanceOf[E]

  def get(id: PK) = null.asInstanceOf[E]

  override implicit def applyCrudOps(t:E) = new MockCrudOps(t)

  class MockCrudOps(t:E) extends PersistentCrudOps(t)
}