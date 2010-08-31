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
package org.brzy.d3

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.annotations.Column
import org.brzy.mvc.action.args.Parameters
import org.brzy.mvc.validator.Validation
import javax.validation.{ConstraintViolation,Validation,Validator,ValidatorFactory}
import org.slf4j.{LoggerFactory, Logger}

/**
 * @author Michael Fortin
 */
class Person( override val id:Long,
		@Column(name="first_name") val firstName:String,
    @Column(name="last_name") val lastName:String) 
		extends KeyedEntity[Long] {
  def this() = this(0, "","")
}


object Person extends Schema {
  val log:Logger = LoggerFactory.getLogger(classOf[Person])
  val persons = table[Person]
	
	class EntityCrudOps(t:Person) {

    def validate() ={
      log.trace("validity")
			val factory = Validation.buildDefaultValidatorFactory
			val validator = factory.getValidator
			val constraintViolations = validator.validate(t)
      new Validation()
    }

    def save() = {
      log.trace("save")
			persons.insert(t)
    }

		def update():Unit = {
      log.trace("save")
			persons.update(t)
    }

    def delete() = {
      log.trace("delete")
			persons.deleteWhere(db => db.id === t.id)
    }
  }

  implicit def applyCrudOps(t:Person) = new EntityCrudOps(t)

  def get(id:Long) = from(persons)(s => where(s.id === id) select(s)).head
	def make(params:Parameters):Person ={
		val person = classOf[Person].newInstance
		person
	}
}