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
package org.brzy.mvc.mock

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Column
import org.squeryl.KeyedEntity

import java.beans.ConstructorProperties

import org.brzy.mod.squeryl.SquerylDao
import org.brzy.validator.Validator
import org.brzy.validator.constraints.{NotNull, Size}
import javax.validation.ConstraintViolation
import collection.immutable.Set

@ConstructorProperties(Array("id", "firstName", "lastName"))
class Person(override val id: Long = 0,
    @Column(name = "first_name") val firstName: String = "",
    @Column(name = "last_name") val lastName: String = "")
    extends KeyedEntity[Long] {
}

object Person extends SquerylDao[Person] {
  override def valid(t:Person) = Validator(t)
      .check("firstName", NotNull(), Size(2 to 36))
      .check("lastName", NotNull(), Size(2 to 36))
      .violations
}