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
package org.brzy.webapp.mock

import javax.validation.constraints.{NotNull,Size}
import org.brzy.validator.Validator
import javax.validation.ConstraintViolation
import org.brzy.persistence.Dao
//import javax.persistence._


/**
 * @author Michael Fortin
 */
@serializable
class MockUser {
  var id:Long = _
  var version:Int = _
  @NotNull @Size(max=30)
  var name:String = _
}

object MockUser extends Dao[MockUser,Long]{

	class EntityCrudOps(t:MockUser) extends PersistentCrudOps(t) {
		def validate() = Validator(t).violations
    def insert(commit:Boolean  = false) = {}
    def commit = {}
    def update = new MockUser()
    def delete = {}
	}

  def newPersistentCrudOps(t: MockUser) = new EntityCrudOps(t)
  implicit def applyCrudOps(t:MockUser) = new EntityCrudOps(t)
	def apply(id:Long) = new MockUser()
	def get(id:Long) = Option(new MockUser())
  def getOrElse(id: Long, alternate: MockUser) = alternate
  def count():Long = 1
  def list = List()
	def list(start:Int, size:Int) = List()
  def load(id:String) = new MockUser()

}