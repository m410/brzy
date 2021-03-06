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

import org.brzy.webapp.persistence.{RichCrudOps, Store}
import org.brzy.webapp.persistence.DefaultTransaction
import org.brzy.webapp.action.args.{Principal, Parameters}
import org.brzy.webapp.action.Constraint
import org.brzy.webapp.action.response._


import scala.reflect._
import scala.language.reflectiveCalls
import scala.language.implicitConversions

import java.lang.{Long=>Lng}
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
abstract class CrudController[PK:Manifest, E<:{def id:PK}:Manifest](val basePath: String) extends Controller  {

  def store:Store[PK,E]

  implicit def crudOps(e:E):RichCrudOps[E]

  def toId(s:String):PK

  /**
   * Uses reflection to get the name of this class without the controller suffix.
   * @return
   */
  def viewBasePath = {
    val simpleName = this.getClass.getSimpleName
    val dir = simpleName.substring(0, simpleName.indexOf("Controller")).toLowerCase
    s"/$dir/"
  }

  val constraints = Seq.empty[Constraint]

  val transaction = DefaultTransaction()


  val entityName = {
    val name = classTag[E].runtimeClass.getSimpleName
    name.substring(0, 1).toLowerCase + name.substring(1)
  }

  def onUpdateToView(e:E):Direction = Redirect(s"/$basePath/${e.id}")

  def onSaveToView(e:E):Direction = Redirect(s"/$basePath/${e.id}")

  def onDeleteToView:Direction = Redirect(s"/$basePath" )

  override def actions = List(
    get("",  list _, View(viewBasePath + "list")) ,
    get("{id}", view _, View(viewBasePath + "view") ) ,
    get("create", create _, View(viewBasePath + "create")) ,
    post("save", save _, View(viewBasePath + "create")) ,
    get("{id}/edit", edit _, View(viewBasePath + "edit") ) ,
    post("{id}/update", update _, View(viewBasePath + "edit")) ,
    post("{id}/delete", delete _, View(viewBasePath + "list"))
  )

  def defaultListSort = "id"
  def defaultListOrder = "desc"
  def defaultListLimit = "50"

  def list(p:Parameters) = {
    val start = p.requestAndUrl.getOrElse("start","0").toInt
    val limit = p.requestAndUrl.getOrElse("size", defaultListLimit).toInt
    val sort = p.requestAndUrl.getOrElse("sort", defaultListSort)
    val order = p.requestAndUrl.getOrElse("order", defaultListOrder)
    Model("table" -> Table(store.list(limit,start, sort, order), start, limit, store.count, sort, order))
  }

  def view(p: Parameters, r: Principal) = entityName -> store(toId(p("id")))

  def create() = entityName -> store.blankInstance

  def save(p: Parameters, r: Principal) = {
    val cmap:Map[String,String] = p.request.map(n=>{n._1->n._2(0)})
    val entity = store.make(cmap)
    entity.validate match {
      case Some(violations) =>
        Model(entityName -> entity, "violations" -> violations)
      case _ =>
        entity.insert(commit = true)
        val redirect = onSaveToView(entity)
        val flash = Flash("New " + entityName + " saved.", entityName + ".save")
        (redirect, flash)
    }
  }

  def edit(p: Parameters, r: Principal) = entityName -> store(toId(p("id")))

  def update(p: Parameters, r: Principal) = {
    val entity = store.make(p.requestAndUrl)
    entity.validate match {
      case Some(violations) =>
        Model(entityName -> entity, "violations" -> violations)
      case _ =>
        entity.update()
        val redirect = onUpdateToView(entity)
        val flash = Flash(entityName + " updated.", entityName + ".update")
        (redirect, flash)
    }
  }

  def delete(p: Parameters, r: Principal) = {
    val entity = store(toId(p("id")))
    entity.delete()
    val redirect = onDeleteToView
    val flash = Flash(entityName + " was Deleted.", entityName + ".delete")
    (redirect, flash)
  }
}