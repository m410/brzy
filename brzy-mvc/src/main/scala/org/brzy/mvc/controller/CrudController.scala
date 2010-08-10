package org.brzy.mvc.controller

import org.brzy.mvc.action.args.Parameters
import org.brzy.mvc.action.returns.{Redirect, Flash, Model, View}
import org.brzy.persistence.{PersistentCrudOps, Persistable, Persistent}
import java.lang.String

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
abstract class CrudController[E <: Persistent[_], PK]()(implicit m: Manifest[E]) {
  val persist: Persistable[E, PK]
  private val entityClass = m.erasure
  private val entityName = {
    val name = entityClass.getName
    name.substring(0,1).toLowerCase + name.substring(1,name.length) 
  }

  implicit def applyCrudOps(e: E) = new PersistentCrudOps(e)

  @Path("")
  def list = entityName + "sList" -> persist.list

  @Path("{id}")
  def show(params: Parameters) = entityName -> persist.load(params("id")(0))

  @Path("create")
  def create() = entityName -> persist.construct

  @Path("save")
  def save(p: Parameters) = {
    val entity = persist.construct(p) 
    val validation = entity.validate

    if (validation.passes) {
      entity.insert
      (Redirect("/" + entityName + "s/" + entity.id), Flash(entityName + ".save", "Saved"))
    }
    else {
      (Model(entityName -> entity, "validation" -> validation), View("/" + entityName + "s/create"))
    }
  }

  @Path("{id}/edit")
  def edit(params: Parameters) = entityName -> persist.load(params("id")(0))

  @Path("{id}/update")
  def update(p: Parameters) = {
    val entity = persist.construct(p)
    val validation = entity.validate

    if (validation.passes) {
      entity.update
      (Redirect("/" + entityName + "s/" + entity.id), Flash(entityName + ".update", "Update"))
    }
    else {
      (Model(entityName -> entity, "validation" -> validation), View("/" + entityName + "s/create"))
    }
  }
}