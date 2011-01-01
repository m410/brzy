package org.brzy.webapp.controller

/**
 * //  def secure(action:()=>AnyRef, principal:SecurityPrincipal):AnyRef = action()
 */
trait Secured {
  val roles = Roles("ROLE_ADMIN","ROLE_USER")
}