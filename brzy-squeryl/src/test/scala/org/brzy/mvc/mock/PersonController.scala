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

import org.brzy.webapp.controller.{Action, Controller}
import org.brzy.webapp.action.args.Parameters

@Controller("persons")
class PersonController {

  @Action("")
  def list =  "personsList"->Person.list()

  @Action("{id}")
  def get(params:Parameters) =  "person"->Person.get(params("id").toLong)

  @Action("create")
  def create() = "person"->new Person

  @Action("save")
  def save(p:Parameters) = {
    val person = new Person(0, p("firstName"), p("lastName"))
		person.insert()
    "person"->person
  }

  @Action("{id}/edit")
  def edit(params:Parameters) =  "person"->Person.get(params("id").toLong)


  @Action("{id}/update")
  def update(p:Parameters) = {
		val person = new Person(p("id")(0).toLong, p("firstName"), p("lastName"))
		person.update()
    "person"->person
  }
}