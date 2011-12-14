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
package org.brzy.webapp.action.args

import org.brzy.webapp.action.Roles
import javax.servlet.http.HttpServletRequest
import org.brzy.webapp.controller.Permission


/**
 *	The Logged in name and the roles the user possesses.  Similar to
 *	HttpServletRequest.getUserPrincipal().
 *
 * @param name the user name. 
 * @param roles the roles the user has 
 */
trait Principal  extends Arg {
  def isLoggedIn:Boolean
  def name:String
  def roles:Roles
}

/**
 * Document me..
 */
@serializable
case class PrincipalSession(name:String, roles:Array[String])

/**
 * Document me..
 */
class PrincipalRequest(request:HttpServletRequest) extends Principal {
  def isLoggedIn = {
    request.getSession(false) != null && request.getSession.getAttribute("brzy_principal") != null
  }

  def name = request.getSession.getAttribute("brzy_principal").asInstanceOf[PrincipalSession].name

  def roles = Roles(request.getSession.getAttribute("brzy_principal").asInstanceOf[PrincipalSession].roles:_*)
}
