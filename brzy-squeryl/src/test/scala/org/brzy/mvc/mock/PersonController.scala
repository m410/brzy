package org.brzy.mock

import org.brzy.mvc.action.args.Parameters
import org.brzy.mvc.controller.{Controller,Path}

@Controller("persons")
class PersonController {

  @Path("")
  def list =  "personsList"->Person.list()

  @Path("{id}")
  def get(params:Parameters) =  "person"->Person.get(params("id")(0).toLong)

  @Path("create")
  def create() = "person"->new Person

  @Path("save")
  def save(p:Parameters) = {
    val person = new Person(0, p("firstName")(0), p("lastName")(0))
		person.insert()
    "person"->person
  }

  @Path("{id}/edit")
  def edit(params:Parameters) =  "person"->Person.get(params("id")(0).toLong)


  @Path("{id}/update")
  def update(p:Parameters) = {
		val person = new Person(p("id")(0).toLong, p("firstName")(0), p("lastName")(0))
		person.update()
    "person"->person
  }
}