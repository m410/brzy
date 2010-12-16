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

import org.brzy.mod.calista.Calista

/**
 *
 */
case class Person extends KeyedEntity(
    key:String,
		firstName:String)

/**
 *
 */
object Person {
	
	def session = Calista.value.get
	
  def get(keyId:String):Person = {
		val columns = session.list(mapper.family | keyId)

		if(columns.size > 0)
    	Option(mapper.newInstance(columns))
		else
			Option(null)
  }

	def save(p:Person) = session.batch(mapper.toMutations(p))
	

	val mapper = new Mapper[Person](family="Standard2",key="key")
			.attributes(nameSerializer=Utf8Type, columns=List(
				Column(property="firstName", column="firstName", valueSerializer=Utf8Type)))
}

