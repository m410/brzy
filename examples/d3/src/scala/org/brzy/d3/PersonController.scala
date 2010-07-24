package org.brzy.d3

import org.brzy.action.args._
import org.brzy.action.returns._
import org.brzy.controller.{Controller,Path}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
@Controller("/persons")
class PersonController {
	
  @Path("") 
	def list = "personsList"->Person.list

  @Path("{id}") 
	def show(params:Parameters) = "person"->Person.get(params("id")(0).toLong)

  @Path("create") 
	def create() = "person"->new Person

  @Path("save") 
	def save(p:Parameters) = {
    val person = new Person(0, p("firstName")(0), p("lastName")(0))
		val validation = person.validate
		if(validation.passes) {
			person.insert
			(Redirect("/persons/"+person.id), Flash("person.save","Saved"))
		}
		else {
			(Model("person"->person,"validation"->validation), View("/persons/create"))
		}
  }

  @Path("{id}/edit") 
	def edit(params:Parameters) = "person"->Person.get(params("id")(0).toLong)

  @Path("{id}/update") 
	def update(p:Parameters) = {
		val person = new Person(p("id")(0).toLong, p("firstName")(0), p("lastName")(0))
		val validation = person.validate
		if(validation.passes) {
			person.update
			(Redirect("/persons/"+person.id), Flash("person.update","Update"))
		}
		else {
			(Model("person"->person,"validation"->validation), View("/persons/create"))
		}
  }
}