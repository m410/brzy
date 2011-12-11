package org.brzy.webapp.controller

import org.brzy.webapp.action.Action
import org.brzy.webapp.action.args.Parameters
import org.brzy.webapp.action.response.{Flash, Session, Redirect, Model}
import org.slf4j.LoggerFactory


/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
abstract class AuthenticatorController[T<:Authenticated](permission:Permission[T], path:String)
        extends Controller(path) {

  val log = LoggerFactory.getLogger(getClass)
  
  def loginViewName:String

  val actions = List(
		Action("",loginViewName,login _),
		Action("logout","/",logout _),
		Action("submit",loginViewName,submit _))

	def login(p:Parameters) {
    log.debug("session: {}",p.session.mkString("[",", ","]"))
  }

	def logout = (Redirect("/"),Session.remove("brzy_principal")) // TODO need to invalid the session

  def submit(params:Parameters) = {
	  log.debug("session: {}",params.session.mkString("[",", ","]"))
		if(check(params)) {
			permission.login(params.request("userName")(0),params.request("password")(0)) match {
				case Some(auth) =>
					if(permission.active(auth)) {
            val direct = params.session.get("last_view") match {
							case Some(lv) =>
								log.debug("returning to view:'{}'",lv)
                Redirect(lv.asInstanceOf[String].replaceFirst("\\.brzy",""))
							case _ =>
								Redirect("/")
						}
						val addTo = Session("brzy_principal"->permission.principal(auth))
						val removeFrom = Session.remove("last_view")
						(addTo,removeFrom,direct)
					}
					else {
						Flash("Sory Your Account is not active.","account.disabled")
					}
				case _ =>
					Flash("Invalid User Name or Password","login.invalid")
			}
		}
		else {
			Model("violations"->"Invalid User Name or Password")
		}
	}

	def check(p:Parameters) =
		p.request.get("userName").isDefined && !p.request("userName").equals("") &&
		p.request.get("password").isDefined && !p.request("password").equals("")

}