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