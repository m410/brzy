package org.brzy.mvc.mock

import org.brzy.mvc.action.args.Parameters

import org.brzy.mvc.action.returns._
import org.brzy.mvc.controller.{Action,Controller}


@Controller("userArgs")
class UserArgController(val userService:UserService) {

  @Action("") def list = "userList"->User.list()
  @Action("{id}") def get(prms:Parameters) = "user"->User.get(prms("id")(0).toLong)
	@Action("custom") def custom = "custom"->userService.someMethod
}