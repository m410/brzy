package org.brzy.sample

import javax.ws.rs.Path
import org.brzy.action.args.Parameters
import org.brzy.validator.Validity
import org.brzy.action.returns._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
@Path("/persons")
class PersonController {

  @Path("")
  def list() = "personList"->Person.list()

  @Path("{id}")
	def view(prms:Parameters) = "person"->Person.get(prms("id")(0).toLong)

	@Path("create")
	def create = "person"->new Person()

	@Path("save")
	def save(params:Parameters)() = {
	  def person:Person = Person.make(params)
 		val validity:Validity = person.validity()

		if(validity.isValid) {
      person.save
      (Redirect("/person/"+person.id), Flash("flash.1","Person saved"), Model("person"->person))
    }
		else
      (View("/person/create.jsp"),Model("person"->person, "errors"->validity))
	}

	@Path("{id}/edit")
	def edit(params:Parameters) = "person"->Person.get(params("id")(0).toLong)

	@Path("{id}/update")
	def update(params:Parameters) = {
		def person = Person.make(params)
    val validity:Validity = person.validity()

		if(validity.isValid) {
      person.save
      (Redirect("/persons/" + person.id),Flash("flash.2","Person updated"),Model("person"->person))
    }
		else
      (View("/person/edit.jsp"),Model("person"->person, "errors"->validity))
	}

	@Path("{id}/delete")
	def delete(params:Parameters) = {
    Person.get(params("id")(0).toLong).delete
		(Redirect("/persons"),Flash("message2","person deleted"))
	}
	
	// @Path("personService")
	// def service = "personService"->personService.hello
}