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
import org.brzy.webapp.action.Action
import org.brzy.persistence.Dao
import org.brzy.webapp.action.args.Parameters
import org.brzy.webapp.action.response.{Flash, Redirect, Model}


/**
 * Controller writers can extend this to get all the crud operations in their
 * controller.  The controller has to be associated with a persistent class
 * that implements Persistent and has a companion class that implements
 * Persistable.
 *
 * @see org.brzy.persistence.Persistable
 * @see org.brzy.persistence.Persistent
 * @author Michael Fortin
 */
abstract class CrudController[E <: {def id : PK}, PK](
        override val basePath: String,
        val dao: Dao[E, PK])(implicit m: Manifest[E])
        extends Controller(basePath) {
  val viewBasePath = ""
  private[this] implicit def applyCrudOps(e: E) = dao.newPersistentCrudOps(e)

  private[this] val entityName = {
    val name = m.erasure.getSimpleName
    name.substring(0, 1).toLowerCase + name.substring(1)
  }

  def actions = Action("", viewBasePath + "list", list _) ::
          Action("{id}", viewBasePath + "view", view _) ::
          Action("create", viewBasePath + "create", create _) ::
          Action("save", viewBasePath + "create", save _) ::
          Action("{id}/edit", viewBasePath + "edit", edit _) ::
          Action("{id}/update", viewBasePath + "edit", update _) ::
          Action("{id}/delete", viewBasePath + "list", delete _) ::
          Nil

  def list(p:Parameters) = entityName + "sList" -> dao.list

  def view(params: Parameters) = entityName -> dao.load(params("id").toString)

  def create() = entityName -> dao.construct

  def save(p: Parameters) = {
    val cmap:Map[String,String] = p.request.map(n=>{n._1->n._2(0)})
    val entity = dao.construct(cmap)
    entity.validate match {
      case Some(violations) =>
        Model(entityName -> entity, "violations" -> violations)
      case _ =>
        entity.insert(commit = true)
        val redirect = Redirect("/" + basePath + "/" + entity.id)
        val flash = Flash("New " + entityName + " saved.", entityName + ".save")
        (redirect, flash)
    }
  }

  def edit(params: Parameters) = entityName -> dao.load(params("id").toString)

  def update(p: Parameters) = {
    val cmap:Map[String,String] = p.request.map(n=>{n._1->n._2(0)})
    val entity = dao.construct(cmap)
    entity.validate match {
      case Some(violations) =>
        Model(entityName -> entity, "violations" -> violations)
      case _ =>
        val updated = entity.update()
        val redirect = Redirect("/" + basePath + "/" + updated.id)
        val flash = Flash(entityName + " updated.", entityName + ".update")
        (redirect, flash)
    }
  }

  def delete(p: Parameters) = {
    val entity = dao.load(p("id").toString)
    entity.delete()
    val redirect = Redirect("/" + basePath )
    val flash = Flash(entityName + " was Deleted.", entityName + ".delete")
    (redirect, flash)
  }
}