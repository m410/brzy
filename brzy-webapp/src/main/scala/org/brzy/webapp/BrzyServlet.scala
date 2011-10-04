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

  protected[webapp]  def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("request: {}, context: {}", req.getServletPath, req.getContextPath)
    val actionPath = parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)

    webapp.actions.find(_.path.isMatch(actionPath)) match {
      case Some(action) =>
        log.debug("{} >>> {}", req.getRequestURI, action)
        val args = buildArgs(action, req)
        val principalOption = Principal.get(req)

        if (!action.isConstrained(req)) {
          val result = callActionOrLogin(req, action, principalOption, args)
          handleResults(action, result, req, res)
        }
        else {
          res.sendError(500)
        }
      case _ =>
        res.sendError(404)
    }
  }

  protected[webapp] def callActionOrLogin(req: HttpServletRequest, action: Action, principalOption: Option[Principal], args: List[AnyRef]): AnyRef = {
    if (webapp.useSsl && action.requiresSsl && !req.isSecure) {
      val redirect = req.getRequestURL.replace(0, 4, "https").toString
      log.debug("redirect: {}",redirect)
      Redirect(redirect)
//      Redirect("https://"+req.getServerName+ req.getRequestURI + {if (req.getQueryString != null) "?" + req.getQueryString else ""})
    }
    else if (action.isSecured) {
      if (req.getSession(false) != null) {

        if (action.isAuthorized(principalOption))
          action.execute(args, principalOption)
        else
          toLogin(req)
      }
      else {
        toLogin(req)
      }
    }
    else {
      action.execute(args, principalOption)
    }
  }

  protected[webapp] def toLogin(req: HttpServletRequest): (Redirect, Flash, SessionAdd) = {
    val flash = Flash("session.end", "Session ended, log in again")
    val sessionParam = SessionAdd("last_view" -> req.getRequestURI)
    (Redirect("/auth"), flash, sessionParam)
  }
}