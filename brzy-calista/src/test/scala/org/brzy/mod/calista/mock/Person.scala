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
package org.brzy.mod.calista.mock

import org.brzy.calista.ocm.{ColumnMapping, Dao, KeyedEntity,Attribute}
import java.util.UUID
import org.brzy.calista.serializer.{UuidType, Utf8Type}

/**
 *
 */
case class Person (key:UUID, firstName:String)extends KeyedEntity[UUID]

/**
 *
 */
object Person extends Dao[UUID,Person]{
  def columnMapping = new ColumnMapping[Person]()
      .attributes(Utf8Type,Array(
        Attribute("key",true,UuidType),
        Attribute("firstName")
      ))
}

