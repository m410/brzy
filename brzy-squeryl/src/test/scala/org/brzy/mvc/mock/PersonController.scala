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

import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.args.Parameters

class PersonController extends Controller("persons"){
  val actions = List(
    Action("","list",list _),
    Action("{id}","view",get _),
    Action("create","form",create _),
    Action("save","form",save _),
    Action("{id}/edit","form",edit _),
    Action("{id}/update","form",update _))

  def list =  "personsList"->Person.list

  def get(params:Parameters) =  "person"->Person.get(params.url("id").toLong)

  def create() = "person"->new Person

  def save(p:Parameters) = {
    val person = new Person(0, p.request("firstName")(0), p.request("lastName")(0))
		person.insert()
    "person"->person
  }

  def edit(params:Parameters) =  "person"->Person.get(params.url("id").toLong)

  def update(p:Parameters) = {
		val person = new Person(p.url("id").toLong, p.request("firstName")(0), p.request("lastName")(0))
		person.update()
    "person"->person
  }
}