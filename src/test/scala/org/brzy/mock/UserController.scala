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
package org.brzy.mock

import org.brzy.controller.Controller
import org.brzy.action.response._
import org.brzy.action.args.Parameters
import org.brzy.action.Parser

class UserController extends Controller("users") {
    self: MockUserStore =>

  override val actions = List(
    action("",listAction _, View("list")),
    action("{id:\\d+}",getAction _, View("view")),
    action("create",create _, View("create")),
    get("get",someOther _, View("get")),
    post("post",someOther2 _, View("post")),
    async("async",asyncStart _, View("async")),
    action("xml",xml _),
    action("redirect",redirect _, View("redirect")),
    action("json",json _, View("json")),
    action("json2",json2 _, View("json2")),
    action("error",error _, View("error")),
    action("save",save _, View("save")),
    action("{id:\\d+}/edit",edit _, View("edit")),
    action("{id:\\d+}/update",update _, View("update")),
    action("{id:\\d+}/delete",delete _, View("delete")),
    action("{id:\\d+}/companies/{cid:\\d+}",company _, View("companie")),
    action("custom",custom _, View("custom")))
  
  def listAction() = "userList" -> list

  def getAction(p: Parameters) = "user" -> get(p("id").toString.toLong)

  def company(p: Parameters) = "user" -> get(p("id").toString.toLong)

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

  def asyncStart(p:Parameters) = {
    Async({(params)=>println("hello");Text("hello")})
  }

  def json2 = new Json(this) with Parser {
    override def parse = "{\"name\":\"value\"}"
  }

  def error = Error(404, "Not Found")

  def save(params: Parameters)() = {
    def user: MockUser = construct(params.request.map(n=>{n._1->n._2(0)}))

    user.validate match {
      case Some(violations) =>
        (View("/user/create"), Model("user" -> user, "violations" -> violations))
      case _ =>
        user.insert()
        (Redirect("/user/" + user.id), Flash("flash.1", "User saved"), Model("user" -> user))
    }
  }

  def edit(params: Parameters) = "user" -> get(params("id").toString.toLong)

  def update(params: Parameters) = {
    def user = construct(params.request.map(n=>{n._1->n._2(0)}))

    user.validate match {
      case Some(violations) =>
        (View("/user/edit"), Model("user" -> user, "violations" -> violations))
      case _ =>
        user.update()
        (Redirect("/users/" + user.id), Flash("flash.2", "User updated"), Model("user" -> user))
    }
  }

  def delete(params: Parameters) = {
    get(params("id").toString.toLong) match {
      case Some(user) =>
        user.delete()
        Flash("message2", "user deleted")
      case _ =>
        Error(500,"No User found to delete")
    }
  }

  def custom() = {
    val session = Session(List("id" -> "x", "id2" -> "y"),Array("id2"),false)
    (Cookie("id","1"), session, Flash("c", "Hello"), View("/x/y"))
  }
}

