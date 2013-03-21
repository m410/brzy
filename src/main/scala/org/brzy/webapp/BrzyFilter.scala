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

import javax.servlet.{FilterChain, FilterConfig, ServletResponse, ServletRequest, Filter => SFilter}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.slf4j.LoggerFactory

import org.brzy.webapp.application.WebApp

/**
 * Forwards only requests to brzy actions, lets all other requests pass through.
 *
 * @author Michael Fortin
 */
class BrzyFilter extends SFilter {
  private[this] val log = LoggerFactory.getLogger(getClass)

  def init(config: FilterConfig) {
    log.debug("Init Filter: {}", config)
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {

    try {
      val request = req.asInstanceOf[HttpServletRequest]
      val response = res.asInstanceOf[HttpServletResponse]
      val webapp = req.getServletContext.getAttribute("application").asInstanceOf[WebApp]

      // todo need to preserve http status
      webapp.doFilterAction(request)  match {
        case ActOn(action) =>
          log.debug("ActOn({})",action)
          action.trans.doWith(webapp.threadLocalSessions, {()=> chain.doFilter(req,res)})
        case ActOnAsync(action) =>
          log.debug("ActOnAsync({})",action)
          chain.doFilter(req,res)
        case RedirectToSecure(path) =>
          log.debug("RedirectToSecure({})",path)
          response.sendRedirect(path)
        case RedirectToAuthenticate(path,lastView) =>
          log.debug("RedirectToAuthenticate({},{})",Array(path,lastView):_*)
          request.getSession.setAttribute("last_view",lastView)
          response.sendRedirect(path)
        case DispatchTo(path) =>
          log.debug("DispatchTo({})",path)
          req.getRequestDispatcher(path).forward(req, res)
        case NotAnAction =>
          log.debug("NotAnAction({})",request.getRequestURI)
          chain.doFilter(req,res)
      }
    }
    catch {
      case e:Throwable =>
        log.error(e.getMessage,e)
        throw e
    }
  }

  /**
   *
   */
  def destroy() {
    log.trace("Destroy")
  }
}
