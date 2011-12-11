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

import org.brzy.webapp.controller.Controller
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.args.Parameters

class UserArgController(val userService:UserService) extends Controller("userArgs"){
  val actions = List(
    Action("","list",list _),
    Action("{id}","view",get _),
    Action("custom","custom",custom _))

  def list = "userList"->MockUser.list
  def get(prms:Parameters) = "user"->MockUser.get(prms("id").toString.toLong)
	def custom = "custom"->userService.someMethod
}