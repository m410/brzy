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

  private[this] val entityClass = m.erasure
  private[this] val entityName = {
    val name = entityClass.getSimpleName
    name.substring(0,1).toLowerCase + name.substring(1)
  }

  implicit def applyCrudOps(e: E) = new PersistentCrudOps(e)

  @Action("")
  def list = entityName + "sList" -> persist.list

  @Action("{id}")
  def show(params: Parameters) = entityName -> persist.load(params("id"))

  @Action("create")
  def create() = entityName -> persist.construct

  @Action("save")
  def save(p: Parameters) = {
    val entity = persist.construct(p) 
    val validation = entity.validate

    if (validation.passes) {
      entity.insert
      (Redirect("/" + entityName + "s/" + entity.id), Flash(entityName + ".save", "Saved"))
    }
    else {
      (Model(entityName -> entity, "validation" -> validation), View("/" + entityName + "/create"))
    }
  }

  @Action("{id}/edit")
  def edit(params: Parameters) = entityName -> persist.load(params("id"))

  @Action("{id}/update")
  def update(p: Parameters) = {
    val entity = persist.construct(p)
    val validation = entity.validate

    if (validation.passes) {
      entity.update
      (Redirect("/" + entityName + "s/" + entity.id), Flash(entityName + ".update", "Update"))
    }
    else {
      (Model(entityName -> entity, "validation" -> validation), View("/" + entityName + "/create"))
    }
  }
}