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
package org.brzy.controller
import org.brzy.persistence.Dao
import org.brzy.action.args.Parameters
import org.brzy.action.response.{Flash, Redirect, Model}

import scala.reflect._
import scala.language.reflectiveCalls

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
abstract class CrudController[PK, E <: {def id : PK}:Manifest](basePath: String)
        extends Controller(basePath)  {
        self:Dao[E, PK] =>

  val viewBasePath = ""

  private[this] val entityName = {
    val name = classTag[E].runtimeClass.getSimpleName
    name.substring(0, 1).toLowerCase + name.substring(1)
  }

//  override val actions = List(
//    get("",  listAction _, View(viewBasePath + "list")) ,
//    get("{id}", view _, View(viewBasePath + "view") ) ,
//    get("create", create _, View(viewBasePath + "create")) ,
//    post("save", save _, View(viewBasePath + "create")) ,
//    get("{id}/edit", edit _, View(viewBasePath + "edit") ) ,
//    post("{id}/update", update _, View(viewBasePath + "edit")) ,
//    post("{id}/delete", delete _, View(viewBasePath + "list"))
//  )


  def listAction(p:Parameters) = entityName + "sList" -> list

  def view(params: Parameters) = entityName -> load(params("id").toString)

  def create() = entityName -> construct

  def save(p: Parameters) = {
    val cmap:Map[String,String] = p.request.map(n=>{n._1->n._2(0)})
    val entity = construct(cmap)
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

  def edit(params: Parameters) = entityName -> load(params("id").toString)

  def update(p: Parameters) = {
    val cmap:Map[String,String] = p.request.map(n=>{n._1->n._2(0)})
    val entity = construct(cmap)
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
    val entity = load(p("id").toString)
    entity.delete()
    val redirect = Redirect("/" + basePath )
    val flash = Flash(entityName + " was Deleted.", entityName + ".delete")
    (redirect, flash)
  }
}