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
package org.brzy.webapp.action

import args.Principal
import org.brzy.application.WebApp
import Action._
import org.slf4j.LoggerFactory
import javax.servlet.http._
import javax.servlet.{ServletResponse, ServletRequest}
import returns.{Error,Redirect,Flash,SessionAdd}

/**
 * The basic servlet implementation.
 *
 * @author Michael Fortin
 */
class BrzyServlet extends HttpServlet {
  private val log = LoggerFactory.getLogger(classOf[BrzyServlet])

  override def service(req: ServletRequest, res: ServletResponse) = {
    internal(req.asInstanceOf[HttpServletRequest], res.asInstanceOf[HttpServletResponse])
  }

  private def internal(req: HttpServletRequest, res: HttpServletResponse) = {
    val app = getServletContext.getAttribute("application").asInstanceOf[WebApp]
    log.trace("request: {}, context: {}", req.getServletPath, req.getContextPath)
    val actionPath = parseActionPath(req.getRequestURI, req.getContextPath)
    log.trace("action-path: {}", actionPath)

    app.actions.find(_.path.isMatch(actionPath)) match {
      case Some(action) =>
        log.debug("{} >> {}", req.getRequestURI, action)
        val args = buildArgs(action, req)

        val result =
          if (action.isSecure) {
            if (req.getSession(false) != null) {
              val session = req.getSession
              val p = session.getAttribute("brzy_principal").asInstanceOf[Principal]
              if (action.authorize(p))
                action.execute(args,Option(p))
              else
                Error(403, "Not Autorized")
            }
            else{
              val flash = Flash("session.end", "Session ended, log in again")
              val add = SessionAdd("last_view"->req.getRequestURI)
              (Redirect("/auth"),flash)
            }
          }
          else {
            val session = req.getSession
            val p = session.getAttribute("brzy_principal").asInstanceOf[Principal]
            action.execute(args,Option(p))
          }

        handleResults(action, result, req, res)
      case _ =>
        res.sendError(404)
    }
  }
}