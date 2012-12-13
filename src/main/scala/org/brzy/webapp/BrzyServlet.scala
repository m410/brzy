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

import action.Action
import action.args.{Arg, ArgsBuilder, PrincipalRequest, Principal}
import action.response.{ResponseHandler, Session, Flash, Redirect}
import org.brzy.application.WebApp


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
  protected var webapp: WebApp = _

  override def init(config: ServletConfig) {
    webapp = config.getServletContext.getAttribute("application").asInstanceOf[WebApp]
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    internal(req.asInstanceOf[HttpServletRequest], res.asInstanceOf[HttpServletResponse])
  }

  private[this]  def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("request: {}, context: {}", Array(req.getServletPath, req.getContextPath):_*)
    val actionPath = ArgsBuilder.parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)

    webapp.actions.find(_.path.isMatch(actionPath)) match {
      case Some(action) =>
        log.debug("{} >> {}", Array(pathLog(req) , action):_*)
        val args = ArgsBuilder(req,action)
        val principal = new PrincipalRequest(req)

        if (!action.isConstrained(req)) {
          val result = callActionOrLogin(req, action, principal, args)
          ResponseHandler(action, result, req, res)
        }
        else {
          res.sendError(500)
        }
      case _ =>
        res.sendError(404)
    }
  }
  
  private[this] def pathLog(req:HttpServletRequest) = new StringBuilder()
      .append(req.getMethod)
      .append(":")
      .append(req.getRequestURI)
      .append(":")
      .append(if(req.getContentType != null) req.getContentType else "" )


  private[this] def callActionOrLogin(req: HttpServletRequest, action: Action, principal: Principal, args: Array[Arg]): AnyRef = {
    if (webapp.useSsl && action.requiresSsl && !req.isSecure) {
      val buf = req.getRequestURL
      // add https and remove the trailing .brzy extension
      val redirect = buf.replace(0, 4, "https").replace(buf.length() - 5, buf.length(),"").toString
      log.trace("redirect: {}",redirect)
      Redirect(redirect)
    }
    else if (action.isSecured) {
      if (req.getSession(false) != null) {
        log.trace("principal: {}",principal)

        if (action.isAuthorized(principal))
          action.execute(args, principal)
        else
          sendToAuthorization(req)
      }
      else {
        sendToAuthorization(req)
      }
    }
    else {
      action.execute(args, principal)
    }
  }

  /**
   * TODO the redirect path is hard coded here to send them to /auth, that should be configurable
   * some how.
   *
   * @param req The httpServletRequest
   * @return The redirect to the authorization page
   */
  private[this] def sendToAuthorization(req: HttpServletRequest): (Redirect, Flash, Session) = {
    val flash = Flash("Your session has ended. Please login again", "session.end")
    val sessionParam = Session("last_view" -> req.getRequestURI)
    (Redirect("/auth"), flash, sessionParam)
  }
}