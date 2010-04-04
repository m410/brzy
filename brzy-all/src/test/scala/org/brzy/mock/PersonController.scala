package org.brzy.mock

import javax.ws.rs.Path
import org.squeryl.PrimitiveTypeMode._
import org.brzy.action.args.Parameters
import Person._
import java.lang.String

/**
 * @author Michael Fortin
 * @version $Id: $
 */
@Path("persons/")
class PersonController {

  @Path("")
  def list =  "personsList"->from(persons)(a=> select(a)).toList

  @Path("{id}")
  def get(params:Parameters) =  "person"->Person.get(params("id")(0).toLong)

  @Path("create")
  def create() = "person"->new Person(0,"first","last")

  @Path("save")
  def save(p:Parameters) = {
    val person = new Person(0, p("firstName")(0), p("lastName")(0))
		person.save()
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