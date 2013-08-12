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
package org.brzy.webapp.persistence

import java.lang.String
import org.brzy.validator.Validator
import scala.language.implicitConversions

class MockPersistable[E<:{def id:PK},PK] extends Dao[E,PK] {

  def findBy(id: PK)(implicit pk: Manifest[PK], t: Manifest[E]) = null.asInstanceOf[E]

  def getBy(id: PK)(implicit pk: Manifest[PK], t: Manifest[E]) = None

  def getOrElse(id: PK, alternate: E)(implicit pk: Manifest[PK], t: Manifest[E]) = alternate

  def load(id: String)(implicit pk: Manifest[PK], t: Manifest[E]) = null.asInstanceOf[E]

  def list(size: Int, offset: Int, sort: String, order: String)(implicit t: Manifest[E]) = List.empty[E]

  def count(implicit t: Manifest[E]) = 1

  implicit def applyCrudOps(t: E)(implicit m: Manifest[E]) = new MockCrudOps(t)


  class MockCrudOps(t:E) extends PersistentCrudOps(t) {
		def validate = Validator(t).violations
    def insert(commit:Boolean = false) = t
    def commit() {}
    def update(commit:Boolean = false) = t
    def delete() {}
  }
}