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
package org.brzy.webapp.controller

import java.beans.ConstructorProperties
import org.brzy.persistence.MockPersistable
import org.brzy.webapp.action.Action
import org.brzy.webapp.action.args.Parameters
import org.brzy.webapp.action.returns.Model

@ConstructorProperties(Array("id"))
class User(val id: Long, val userName:String, val password:String) extends Authenticated {
  def this() = this(0,"","")
	def authenticatedRoles = Array.empty[String]
}

object User extends MockPersistable[User, Long] with Authenticator[User] {
	def login(user:String,pass:String) = None
}

class UserController extends CrudController("users",User) {
  override val actions = super.actions ++ List(Action("{id}/items/{iid}","itmes",sub _))
  def sub(p:Parameters) = Model("a"->"b")
}

