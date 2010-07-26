package org.brzy.mock

import org.brzy.action.args.Parameters

import org.brzy.action.returns._
import org.brzy.controller.{Path,Controller}


@Controller("userArgs")
class UserArgController(val userService:UserService) {

  @Path("") def list = "userList"->User.list()
  @Path("{id}") def get(prms:Parameters) = "user"->User.get(prms("id")(0).toLong)
	@Path("custom") def custom = "custom"->userService.someMethod
}