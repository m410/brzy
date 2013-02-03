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
package org.brzy.action.args

import org.brzy.action.Roles
import javax.servlet.http.HttpServletRequest


/**
 *	The Logged in name and the roles the user possesses.  Similar to
 *	HttpServletRequest.getUserPrincipal().
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
case class PrincipalSession(name:String, roles:Array[String]) {
  override def toString = "PrincipalSession("+name+", "+roles.mkString(",")+")"
}

/**
 * Used as the default implementation of the Principal action argument.
 * @see Principal
 */
class PrincipalRequest(request:HttpServletRequest) extends Principal {

  /**
   * Check to see if the principal user is logged in.
   * @return true for logged in
   */
  def isLoggedIn = {
    request.getSession(false) != null && request.getSession.getAttribute("brzy_principal") != null
  }

  /**
   * The name of the logged in user, or null.
   * @return
   */
  def name = {
    if (isLoggedIn)
      request.getSession.getAttribute("brzy_principal").asInstanceOf[PrincipalSession].name
    else
      null
  }

  /**
   * The roles of the logged in user
   * @return A Roles constraint object with the names of the roles, or null if not logged in.
   */
  def roles = {
    if (isLoggedIn)
      Roles(request.getSession.getAttribute("brzy_principal").asInstanceOf[PrincipalSession].roles:_*)
    else
      null
  }

  override def toString = {
    val buf = new StringBuilder().append("Principal(")
    buf.append("isLoggedIn=").append(isLoggedIn)
    if (isLoggedIn) {
      buf.append(",name=").append(name)
      buf.append(",roles=").append(roles)
    }
    buf.append(")")
    buf.toString()
  }
}
