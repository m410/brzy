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

import org.brzy.action.response.View
import org.brzy.controller.Controller
import org.brzy.action.args.Parameters


class UserArgController extends Controller("userArgs"){
  self:UserService =>

  override val actions = List(
    action("",listAction _,View("list")),
    action("{id}",getAction _, View("view")),
    action("custom",custom _, View("custom")))

  def listAction = "userList"-> someMethod

  def getAction(prms:Parameters) = "user"-> someMethod

	def custom = "custom"->someMethod
}