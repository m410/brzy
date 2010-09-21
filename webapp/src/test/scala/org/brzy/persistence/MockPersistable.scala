/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.persistence

import java.lang.String
import org.brzy.webapp.action.args.Parameters
import org.brzy.reflect.Construct
import collection.immutable.Map
import javax.validation.Validation


class MockPersistable[E<:Persistent[_],PK] extends Persistable[E,PK] {

  override def construct(p: Parameters)(implicit m:Manifest[E]) = Construct.withCast[E](p)

  def list(size: Int, offset: Int) = Nil

  def list() = Nil

  def load(id: String) = null.asInstanceOf[E]

  def get(id: PK) = null.asInstanceOf[E]

  override implicit def applyCrudOps(t:E) = new MockCrudOps(t)

  class MockCrudOps(t:E) extends PersistentCrudOps(t)
}