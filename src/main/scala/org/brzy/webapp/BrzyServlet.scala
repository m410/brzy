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
package org.brzy.webapp

import org.brzy.application.WebApp

import action._
import action.Action._
import action.Principal

import org.slf4j.LoggerFactory
import javax.servlet.http._
import javax.servlet.{ServletConfig, ServletResponse, ServletRequest}

/**
 * Http servlet that finds and call action requested. If the session is expired,
 * then the login page is displayed.  This also checks the constraints on the
 * action and checks them before calling the action.
 *
 * @author Michael Fortin
 */
class BrzyServlet extends HttpServlet {
  private val log = LoggerFactory.getLogger(classOf[BrzyServlet])
  protected[webapp] var webapp: WebApp = _

  override def init(config: ServletConfig) {
    webapp = config.getServletContext.getAttribute("application").asInstanceOf[WebApp]
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    internal(req.asInstanceOf[HttpServletRequest], res.asInstanceOf[HttpServletResponse])
  }

  protected[this] def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("request: {}, context: {}", req.getServletPath, req.getContextPath)
    val actionPath = parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)

    webapp.actions.find(_.path.isMatch(actionPath)) match {
      case Some(action) =>
        log.debug("{} >>> {}", req.getRequestURI, action)
        val args = buildArgs(action, req)
        // wrap in constraint check
        val result = doAction(req, action, args)
        handleResults(action, result, req, res)
      case _ =>
        res.sendError(404)
    }
  }

  protected[this] def doAction(req: HttpServletRequest, action: Action, args: List[AnyRef]): AnyRef = {
    if (action.isSecured) {
      principal(req) match {
        case Some(p) =>
          if (action.isAuthorized(p))
            action.execute(args, Option(p))
          else
            toLogin(req)
        case _ =>
          toLogin(req)
      }
    }
    else {
      action.execute(args, principal(req))
    }
  }

  protected[this] def principal(req: HttpServletRequest) = {
    Option(
      if (req.getSession(false) != null)
        req.getSession.getAttribute("brzy_principal").asInstanceOf[Principal]
      else
        null
    )
  }

  protected[this] def toLogin(req: HttpServletRequest): (Redirect, Flash, SessionAdd) = {
    val flash = Flash("session.end", "Session ended, log in again")
    val sessionParam = SessionAdd("last_view" -> req.getRequestURI)
    (Redirect("/auth"), flash, sessionParam)
  }
}