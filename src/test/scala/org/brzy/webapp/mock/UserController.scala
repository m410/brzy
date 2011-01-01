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
import org.brzy.webapp.action.Action
import org.brzy.webapp.controller.Controller

class UserController extends Controller("users") {
  val actions = List(
    Action("","list",list _),
    Action("{id}","view",get _),
    Action("create","create",create _),
    Action("other","other",someOther _),
    Action("other2","other2",someOther2 _),
    Action("xml","xml",xml _),
    Action("redirect","redirect",redirect _),
    Action("json","json",json _),
    Action("json2","json2",json2 _),
    Action("error","error",error _),
    Action("save","save",save _),
    Action("{id}/edit","edit",edit _),
    Action("{id}/update","update",update _),
    Action("{id}/delete","delete",delete _),
    Action("{id}/companies/{cid}","companie",company _),
    Action("custom","custom",custom _))
  
  def list() = "userList" -> MockUser.list()

  def get(p: Parameters) = "user" -> MockUser.get(p("id").toLong)

  def company(p: Parameters) = "user" -> MockUser.get(p("id").toLong)

  def create = "user" -> new MockUser()

  def someOther = View("/index")

  def someOther2 = View("/users/page")

  def xml = Xml(UserMock("John"))

  def redirect = Redirect("http://o2l.co")

  def json = {
    val user = new MockUser()
    user.id = 1
    user.name = "hello"
    Json(user)
  }

  def json2 = new Json(this) with Parser {
    override def parse = "{\"name\":\"value\"}"
  }

  def error = Error(404, "Not Found")

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

  def edit(params: Parameters) = "user" -> MockUser.get(params("id")(0).toLong)

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

  def delete(params: Parameters) = {
    MockUser.get(params("id")(0).toLong) match {
      case Some(user) =>
        user.delete
        Flash("message2", "user deleted")
      case _ =>
        Error(500,"No User found to delete")
    }
  }

  def custom() = {
    (CookieAdd("id" -> "1"), SessionAdd("id" -> "x", "id2" -> "y"), SessionRemove("id2"), Flash("c", "Hello"), View("/x/y"))
  }
}

case class UserMock(name:String)