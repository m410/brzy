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

  @Action("") def list() = "userList"->User.list()

  @Action("{id}") def get(prms:Parameters) = "user"->User.get(prms("id")(0).toLong)

	@Action("create") def create = "user"->new User()

  @Action("other") def someOther = View("/index")

  @Action("other2") def someOther2 = View("/users/page")

  @Action("xml") def xml = Xml(this)

  @Action("json") def json = Json(this)

  @Action("json2") def json2 = new Json(this) with Parser {
    override def parse = "{\"name\":\"value\"}"
  }

  @Action("error") def error = Error(404,"Not Found")
  
  @Action("save")
	def save(params:Parameters)() = {
	  def user:User = User.make(params)
 		val validation = user.validity()

		if(validation.passes) {
      user.save
      (Redirect("/user/"+user.id), Flash("flash.1","User saved"), Model("user"->user))
    }
		else
      (View("/user/create.jsp"),Model("user"->user, "errors"->validation))
	}

	@Action("{id}/edit")
	def edit(params:Parameters) = "user"->User.get(params("id")(0).toLong)

	@Action("{id}/update")
	def update(params:Parameters) = {
		def user = User.make(params)
    val validation = user.validity()

		if(validation.passes) {
      user.save
      (Redirect("/users/" + user.id),Flash("flash.2","User updated"),Model("user"->user))
    }
		else
      (View("/user/edit.jsp"),Model("user"->user, "errors"->validation))
	}

	@Action("{id}/delete")
	def delete(params:Parameters) = {
    def user = User.get(params("id")(0).toLong)
		user.delete
    Flash("message2","user deleted")
	}
	
	@Action("custom")
	def custom() = {
		(CookieAdd("id"->"1"),SessionAdd("id"->"x","id2"->"y"),SessionRemove("id2"),Flash("c","Hello"),View("/x/y"))
	}
}