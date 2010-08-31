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
package org.brzy.cascal.mock

import com.shorrockin.cascal.serialization.annotations.{Key, Keyspace, Family,Value}
import com.shorrockin.cascal.serialization.Converter
import org.brzy.cascal.Cascal
import com.shorrockin.cascal.utils.Conversions._


@Keyspace("Keyspace1") // Driiiv
@Family("Standard1") // Hit|Creative|Suppression
case class Person(
    @Key key:String,
		@Value("firstName") firstName:String)

object Person {
  def get(keyId:String):Person = {
    println("key: " + keyId)
    val session = Cascal.value.get
    val results = session.list("Keyspace1" \ "Standard2" \ keyId)
    Converter[Person](results)
  }
}

