package org.brzy.controller

import org.brzy.action.args.Parameters
import org.brzy.action.response._

import org.slf4j.LoggerFactory


/**
 * Simplifies secure login by extending this controller
 *
 * @author Michael Fortin
 */
@deprecated("Use AuthController","0.11")
abstract class AuthenticatorController[T <: Authenticated]( path: String) extends Controller {
  self:Permission[T] =>

  val log = LoggerFactory.getLogger(getClass)

  val loginViewName: String = "/auth/login"

  val logoutViewName: String = "/"

  override val actions = List(
    get("", loginAction _, View(loginViewName)),
    get("logout", logout _, View(logoutViewName)),
    post("submit", submit _, View(loginViewName)))

  def loginAction(p: Parameters) {
    p.session match {
      case Some(s) => log.debug("session: {}", s.mkString("[", ", ", "]"))
      case _ => log.debug("session: {}", "None")
    }
  }

  def logout = (Redirect("/"), Session.invalidate)

  def submit(p: Parameters) = {
    log.debug("session: {}", p.session.get.mkString("[", ", ", "]"))
    if (check(p)) {
      login(p("userName"), p("password")) match {
        case Some(auth) => authOrRejectResponse(auth, p)
        case _ => Flash("Invalid User Name or Password", "login.invalid")
      }
    }
    else {
      Model("violations" -> "Invalid User Name or Password")
    }
  }

  private def authOrRejectResponse(auth: T, p: Parameters): Product = {
    if (active(auth)) {
      val direct = directWithSession(p)
      val addTo = Session("brzy_principal" -> principal(auth))
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