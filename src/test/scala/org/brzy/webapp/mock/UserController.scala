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
package org.brzy.webapp.mock

import org.brzy.webapp.action.args.Parameters

import org.brzy.webapp.action.returns._
import org.brzy.webapp.controller.{Action, Controller}

@Controller("users")
class UserController {
  @Action("") def list() = "userList" -> MockUser.list()

  @Action("{id}") def get(p: Parameters) = "user" -> MockUser.get(p("id").toLong)

  @Action("create") def create = "user" -> new MockUser()

  @Action("other") def someOther = View("/index")

  @Action("other2") def someOther2 = View("/users/page")

  @Action("xml") def xml = Xml(this)

  @Action("redirect") def redirect = Redirect("http://o2l.co")

  @Action("json") def json = {
    val user = new MockUser()
    user.id = 1
    user.name = "hello"
    Json(user)
  }

  @Action("json2") def json2 = new Json(this) with Parser {
    override def parse = "{\"name\":\"value\"}"
  }

  @Action("error") def error = Error(404, "Not Found")

  @Action("save")
  def save(params: Parameters)() = {
    def user: MockUser = MockUser.construct(params)

    user.validate match {
      case Some(violations) =>
        (View("/user/create"), Model("user" -> user, "violations" -> violations))
      case _ =>
        user.insert()
        (Redirect("/user/" + user.id), Flash("flash.1", "User saved"), Model("user" -> user))
    }
  }

  @Action("{id}/edit")
  def edit(params: Parameters) = "user" -> MockUser.get(params("id")(0).toLong)

  @Action("{id}/update")
  def update(params: Parameters) = {
    def user = MockUser.construct(params)

    user.validate match {
      case Some(violations) =>
        (View("/user/edit"), Model("user" -> user, "violations" -> violations))
      case _ =>
        user.update()
        (Redirect("/users/" + user.id), Flash("flash.2", "User updated"), Model("user" -> user))
    }
  }

  @Action("{id}/delete")
  def delete(params: Parameters) = {
    MockUser.get(params("id")(0).toLong) match {
      case Some(user) =>
        user.delete
        Flash("message2", "user deleted")
      case _ =>
        Error(500,"No User found to delete")
    }
  }

  @Action("custom")
  def custom() = {
    (CookieAdd("id" -> "1"), SessionAdd("id" -> "x", "id2" -> "y"), SessionRemove("id2"), Flash("c", "Hello"), View("/x/y"))
  }
}