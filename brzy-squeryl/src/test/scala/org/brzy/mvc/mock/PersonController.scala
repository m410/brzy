package org.brzy.mock

import org.brzy.mvc.action.args.Parameters
import org.brzy.mvc.controller.{Controller,Action}

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