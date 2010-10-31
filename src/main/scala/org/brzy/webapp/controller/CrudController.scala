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
package org.brzy.webapp.controller

import org.brzy.webapp.action.args.Parameters
import org.brzy.webapp.action.returns.{Redirect, Flash, Model, View}
import org.brzy.persistence.{Persistable, Persistent}
import org.slf4j.LoggerFactory

/**
 * Controller writers can extend this to get all the crud operations in their controller.  The
 * controller has to be associated with a persistent class taht implements Persistent and has
 * a companion class that implements Persistable.
 *
 * @see org.brzy.persistence.Persistable
 * @see org.brzy.persistence.Persistent
 * @author Michael Fortin
 */
abstract class CrudController[E <: Persistent[_], PK]()(implicit m: Manifest[E]) {
  val log = LoggerFactory.getLogger(this.getClass)
  val persist: Persistable[E, PK]

  implicit def applyCrudOps(e: E) = persist.newPersistentCrudOps(e)

  private[this] val entityClass = m.erasure

  private[this] val entityName = {
    val name = entityClass.getSimpleName
    name.substring(0, 1).toLowerCase + name.substring(1)
  }

  @Action("") def list = entityName + "sList" -> persist.list

  @Action("{id}") def show(params: Parameters) = entityName -> persist.load(params("id"))

  @Action("create") def create() = entityName -> persist.construct

  @Action("save") def save(p: Parameters) = {
    val entity = persist.construct(p.toMap)
    entity.validate match {
      case Some(violations) =>
        (Model(entityName -> entity, "violations" -> violations), View("/" + entityName + "/create"))
      case _ =>
        entity.insert(commit = true)
        (Redirect("/" + entityName + "s/" + entity.id), Flash("New " + entityName + " saved.", entityName + ".save"))
    }
  }

  @Action("{id}/edit") def edit(params: Parameters) = entityName -> persist.load(params("id"))

  @Action("{id}/update") def update(p: Parameters) = {
    val entity = persist.construct(p.toMap)
    entity.validate match {
      case Some(violations) =>
        (Model(entityName -> entity, "violations" -> violations), View("/" + entityName + "/create"))
      case _ =>
        val updated = entity.update()
        (Redirect("/" + entityName + "s/" + updated.id), Flash(entityName + " updated.", entityName + ".update"))
    }
  }

  @Action("{id}/delete") def delete(p: Parameters) = {
    val entity = persist.load(p("id"))
    entity.delete
    (Redirect("/" + entityName + "s"), Flash(entityName + " was Deleted.", entityName + ".delete"))
  }
}