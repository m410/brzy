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
package org.brzy.mock

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Column
import org.squeryl.KeyedEntity
import java.beans.ConstructorProperties
import org.brzy.mod.squeryl.SquerylPersistence
import javax.validation.constraints.{Size, NotNull}
import org.brzy.persistence.Persistent

@ConstructorProperties(Array("id", "firstName", "lastName"))
class Person(override val id: Long,
             @Column(name = "first_name") @NotNull @Size(min = 4, max = 24) val firstName: String,
             @Column(name = "last_name") @NotNull @Size(min = 4, max = 24) val lastName: String)
        extends KeyedEntity[Long] with Persistent[Long] {
  def this() = this (0, "", "")
}

object Person extends SquerylPersistence[Person]