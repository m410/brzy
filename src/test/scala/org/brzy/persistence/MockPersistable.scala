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
import org.brzy.validator.Validator
import javax.validation.ConstraintViolation


class MockPersistable[E<:{def id:PK},PK] extends Dao[E,PK] {

  def list(size: Int, offset: Int) = Nil

  def list() = Nil

  def load(id: String) = null.asInstanceOf[E]

  def apply(id: PK) = null.asInstanceOf[E]

  def get(id: PK) = None

  def getOrElse(id: PK, alternate: E) = alternate

  override implicit def applyCrudOps(t:E) = new MockCrudOps(t)

  def newPersistentCrudOps(t: E) = new MockCrudOps(t)

  def count():Long = 0

  class MockCrudOps(t:E) extends PersistentCrudOps(t) {
		def validate() = Validator(t).violations
    def insert(commit:Boolean = false) = {}
    def commit = {}
    def update():E = t
    def delete = {}
    def discard = {}
  }
}