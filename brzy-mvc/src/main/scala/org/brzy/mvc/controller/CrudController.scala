package org.brzy.mvc.controller

import org.brzy.mvc.action.args.Parameters
import org.brzy.mvc.action.returns.{Redirect, Flash, Model, View}
import org.brzy.persistence.{PersistentCrudOps, Persistable, Persistent}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
abstract class CrudController[E <: Persistent[_],PK]()(implicit m:Manifest[E]) {
  val persist:Persistable[E,PK]
  implicit def applyCrudOps(e:E) = new PersistentCrudOps(e)

  
  @Path("")
	def list = "personsList"->persist.list

  @Path("{id}")
	def show(params:Parameters) = "person"->persist.load(params("id")(0))

  @Path("create")
	def create() = "person"->persist.construct

  @Path("save")
	def save(p:Parameters) = {
    val person = persist.construct(p)
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
	def edit(params:Parameters) = "person"->persist.load(params("id")(0))

  @Path("{id}/update")
	def update(p:Parameters) = {
		val person = persist.construct(p)
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