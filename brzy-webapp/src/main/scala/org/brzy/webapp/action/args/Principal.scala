package org.brzy.webapp.action.args

import org.brzy.webapp.action.Roles
import javax.servlet.http.HttpServletRequest


/**
 *	The Logged in name and the roles the user posseses.  Similar to HttpServletRequest.getUserPrincipal().
 * @param name the user name. 
 * @param roles the roles the user has 
 */
trait Principal  extends Arg {
  def isLoggedIn:Boolean
  def name:String
  def roles:Roles
}

class PrincipalRequest(request:HttpServletRequest) extends Principal {
  def isLoggedIn = request.getUserPrincipal != null

  def name = request.getUserPrincipal.getName

  def roles = Roles("")
}
