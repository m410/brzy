package org.brzy.mock

import org.brzy.action.args.Parameters
import org.brzy.validator.Validation
import org.brzy.action.returns._
import org.brzy.controller.{Path, Controller}


@Controller("users")
class UserController {

  @Path("")
  def list() = "userList"->User.list()

  @Path("{id}")
	def get(prms:Parameters) = "user"->User.get(prms("id")(0).toLong)

	@Path("create")
	def create = "user"->new User()

  @Path("save")
	def save(params:Parameters)() = {
	  def user:User = User.make(params)
 		val validity = user.validity()

		if(validity.passes) {
      user.save
      (Redirect("/user/"+user.id), Flash("flash.1","User saved"), Model("user"->user))
    }
		else
      (View("/user/create.jsp"),Model("user"->user, "errors"->validity))
	}

	@Path("{id}/edit")
	def edit(params:Parameters) = "user"->User.get(params("id")(0).toLong)

	@Path("{id}/update")
	def update(params:Parameters) = {
		def user = User.make(params)
    val validity = user.validity()

		if(validity.passes) {
      user.save
      (Redirect("/users/" + user.id),Flash("flash.2","User updated"),Model("user"->user))
    }
		else
      (View("/user/edit.jsp"),Model("user"->user, "errors"->validity))
	}

	@Path("{id}/delete")
	def delete(params:Parameters) = {
    def user = User.get(params("id")(0).toLong)
		user.delete
    Flash("message2","user deleted")
	}
	
	@Path("custom")
	def custom() = {
		(CookieAdd("id"->"1"),SessionAdd("id"->"x","id2"->"y"),SessionRemove("id2"),Flash("c","Hello"),View("/x/y"))
	}
}