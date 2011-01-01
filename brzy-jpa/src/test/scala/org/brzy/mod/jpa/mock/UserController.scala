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
package org.brzy.mod.jpa.mock

import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.returns._
import org.brzy.webapp.action.args.Parameters


class UserController extends Controller("users"){
  val actions = List(
    Action("","list",list _),
    Action("{id}","view",get _),
    Action("create","form",create _),
    Action("save","form",save _),
    Action("{id}/edit","form",edit _),
    Action("{id}/update","form",update _),
    Action("{id}/delete","form",delete _),
    Action("custom","custom",custom _))

  def list() = "userList" -> User.list()

  def get(prms: Parameters) = "user" -> User.get(prms("id")(0).toLong)

  def create = "user" -> new User()

  def save(params: Parameters)() = {
    def user = User.construct(params.toMap)

    user.validate match {
      case Some(violations) =>
        (View("/user/create.jsp"), Model("user" -> user, "violations" -> violations))
      case _ =>
        user.insert()
        (Redirect("/user/" + user.id), Flash("flash.1", "User saved"), Model("user" -> user))
    }
  }

  def edit(params: Parameters) = "user" -> User.get(params("id")(0).toLong)

  def update(params: Parameters) = {
    def user = User.construct(params.toMap)

    user.validate match {
      case Some(violations) =>
        (View("/user/edit.jsp"), Model("user" -> user, "violations" -> violations))
      case _ =>
        user.update()
        (Redirect("/users/" + user.id), Flash("flash.2", "User updated"), Model("user" -> user))
    }
  }

  def delete(params: Parameters) = {
    User.get(params("id")(0).toLong) match {
      case Some(user) =>
        user.delete
        Flash("message2", "user deleted")
      case _ =>
        Error(500, "No entity found")
    }
  }

  def custom() = {
    (CookieAdd("id" -> "1"), SessionAdd("id" -> "x", "id2" -> "y"), SessionRemove("id2"), Flash("c", "Hello"), View("/x/y"))
  }
}