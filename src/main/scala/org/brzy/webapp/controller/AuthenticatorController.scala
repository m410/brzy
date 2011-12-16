package org.brzy.webapp.controller

import org.brzy.webapp.action.Action
import org.brzy.webapp.action.args.Parameters
import org.brzy.webapp.action.response.{Flash, Session, Redirect, Model}
import org.slf4j.LoggerFactory


/**
 * Simplifies secure login by extending this controller
 *
 * @author Michael Fortin
 */
abstract class AuthenticatorController[T <: Authenticated](permission: Permission[T], path: String)
        extends Controller(path) {

  val log = LoggerFactory.getLogger(getClass)

  val loginViewName: String = "/auth/login"

  val logoutViewName: String = "/"

  val actions = List(
    Action("", loginViewName, login _),
    Action("logout", logoutViewName, logout _),
    Action("submit", loginViewName, submit _))

  def login(p: Parameters) {
    p.session match {
      case Some(s) => log.debug("session: {}", s.mkString("[", ", ", "]"))
      case _ => log.debug("session: {}", "None")
    }
  }

  def logout = (Redirect("/"), Session.invalidate)

  def submit(p: Parameters) = {
    log.debug("session: {}", p.session.get.mkString("[", ", ", "]"))
    if (check(p)) {
      permission.login(p("userName"), p("password")) match {
        case Some(auth) => authOrRejectResponse(auth, p)
        case _ => Flash("Invalid User Name or Password", "login.invalid")
      }
    }
    else {
      Model("violations" -> "Invalid User Name or Password")
    }
  }

  private def authOrRejectResponse(auth: T, p: Parameters): Product = {
    if (permission.active(auth)) {
      val direct = directWithSession(p)
      val addTo = Session("brzy_principal" -> permission.principal(auth))
      val removeFrom = Session.remove("last_view")
      (addTo, removeFrom, direct)
    }
    else {
      Flash("Sory Your Account is not active.", "account.disabled")
    }
  }

  private def check(p: Parameters) =
    p.get("userName").isDefined && !p("userName").equals("") &&
            p.get("password").isDefined && !p("password").equals("")

  private def directWithSession(p: Parameters): Redirect = {
    p.session match {
      case Some(session) => session.get("last_view") match {
        case Some(lastView) =>
          log.debug("returning to view:'{}'", lastView)
          Redirect(lastView.asInstanceOf[String].replaceFirst("\\.brzy", ""))
        case _ =>
          Redirect("/")
      }
      case _ => Redirect("/")
    }
  }
}