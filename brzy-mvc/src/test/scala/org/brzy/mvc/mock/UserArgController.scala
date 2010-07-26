package org.brzy.mvc.mock

import org.brzy.mvc.action.args.Parameters

import org.brzy.mvc.action.returns._
import org.brzy.mvc.controller.{Path,Controller}


@Controller("userArgs")
class UserArgController(val userService:UserService) {

  @Path("") def list = "userList"->User.list()
  @Path("{id}") def get(prms:Parameters) = "user"->User.get(prms("id")(0).toLong)
	@Path("custom") def custom = "custom"->userService.someMethod
}