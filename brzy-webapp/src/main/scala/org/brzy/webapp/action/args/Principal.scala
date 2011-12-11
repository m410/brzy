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
 *
 */
@serializable
case class PrincipalSession(name:String, roles:Array[String])

/**
 *
 */
class PrincipalRequest(request:HttpServletRequest) extends Principal {
  def isLoggedIn = {
    request.getSession(false) != null && request.getSession.getAttribute("brzy_principal") != null
  }

  def name = request.getSession.getAttribute("brzy_principal").asInstanceOf[PrincipalSession].name

  def roles = Roles(request.getSession.getAttribute("brzy_principal").asInstanceOf[PrincipalSession].roles:_*)
}
