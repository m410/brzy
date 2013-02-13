package org.brzy.webapp.controller

import org.brzy.webapp.action.response._
import org.brzy.webapp.action.args.{Principal, Parameters}
import org.slf4j.LoggerFactory
import org.brzy.webapp.action.Constraint
import org.brzy.webapp.persistence.DefaultTransaction


/**
 * The default auth controller.  This is not required to authenticate a user, but fulfills the
 * most common use cases.  To use it, extend it like this:
 * {{{
 * class AuthController(s:SecurityService) extends DefaultAuthController(s,"auth") {
 *   override val loginForm = "/myCustomFormLocation"
 * }
 * }}}
 *
 * Or simply add it directly to your application:
 *
 * {{{
 * class Application(config:WebAppConfig) extends WebApp(config) {
 *  lazy val securityService = proxyInstance[SecurityService]()
 *  def makeServices = List(securityService)
 *  def makeControllers = List(proxyInstance[DefaultAuthController](securityService,"auth"))
 * }
 * }}}
 *
 * @see Permission
 * @see Authorized
 * @see Authority
 * @author Michael Fortin
 */
class AuthController[T <: Authenticated](val basePath: String) extends Controller {
    self:Permission[T] =>

  private[this] val log = LoggerFactory.getLogger(classOf[AuthController[T]])

  val constraints = Seq.empty[Constraint]

  val transaction = DefaultTransaction()

  /**
   * override to change the form of the login page
   */
  def loginForm = "/login"

  /**
   * override to change the redirect target page of the user once they've logged in successfully.
   */
  def onSuccessfulLoginRedirectTo = "/"

  /**
   * override to change the destination the user goes once they logout.
   */
  def onLogoutRedirectTo = "/"

  override val actions = List(
    get("", loginAction _, View(loginForm)),
    get("logout", logout _, View(onLogoutRedirectTo)),
    post("submit", submit _, View(loginForm))
  )

  def loginAction(p: Parameters) {
    p.session match {
      case Some(s) => log.debug("session: {}", s.mkString("[", ", ", "]"))
      case _ => log.debug("session: {}", "None")
    }
  }

  def logout(principal: Principal) = {
    onExplicitLogout(principal)
    (Redirect(onLogoutRedirectTo), Session.invalidate)
  }

  def submit(p: Parameters) = {
    if (check(p)) {
      login(p("userName"), p("password")) match {
        case Some(auth) =>
          onSuccessfulLogin(auth)
          authOrRejectResponse(auth, p)
        case _ =>
          onFailedLogin()
          Flash("Invalid User Name or Password", "login.invalid")
      }
    }
    else {
      val flash = Flash("Missing Username and Password", "login.invalid")
      val model = Model("violations" -> "Invalid User Name or Password")
      (model, flash)
    }
  }

  /**
   * helper for subclasses to implement
   * @param authenticated
   */
  def onSuccessfulLogin(authenticated: T) {
  }

  /**
   * helper for subclasses to implement
   */
  def onFailedLogin() {
  }

  /**
   * helper for subclasses to implement
   * @param principal
   */
  def onExplicitLogout(principal: Principal) {
  }

  private def authOrRejectResponse(auth: T, p: Parameters): Product = {
    if (active(auth)) {
      val direct = directWithSession(p)
      val attrs = List("brzy_principal" -> principal(auth))
      val removers = Array("last_view")
      val session = Session(add = attrs, remove = removers, invalidate = false)
      (session, direct)
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
      case Some(session) =>
        session.get("last_view") match {
          case Some(lastView) =>
            Redirect(lastView.asInstanceOf[String].replaceFirst("\\.brzy", ""))
          case _ =>
            Redirect(onSuccessfulLoginRedirectTo)
        }
      case _ =>
        Redirect(onSuccessfulLoginRedirectTo)
    }
  }
}
