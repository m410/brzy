package org.brzy.mod.scalate.view

import javax.servlet.http.HttpServletRequest
import org.brzy.webapp.action.Principal

/**
 * Helpers view functions for security
 * 
 * @author Michael Fortin
 */
object SecureFunctions {
  def isAuthorized(roles:String*)(implicit req:HttpServletRequest):Boolean =
      if(req.getSession(false) == null) {
        false
      }
      else {
        val principal = req.getSession(false).getAttribute("brzy_principal").asInstanceOf[Principal]
        principal.roles.allowed.forall(role=>roles.contains(role))
      }

  def isLoggedIn()(implicit req:HttpServletRequest):Boolean =
      if(req.getSession(false) == null) {
        false
      }
      else {
        req.getSession(false).getAttribute("brzy_principal") != null
      }

}