package org.brzy.sample

import javax.ws.rs.Path
import org.brzy.action.args.Parameters
import org.brzy.validator.Validity
import org.brzy.action.returns._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
@Path("users")
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
 		val validity:Validity = user.validity()

		if(validity.isValid) {
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
    val validity:Validity = user.validity()

		if(validity.isValid) {
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
}