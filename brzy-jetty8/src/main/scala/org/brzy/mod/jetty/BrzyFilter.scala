package org.brzy.mod.jetty

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

import javax.servlet.{FilterChain, FilterConfig, ServletResponse, ServletRequest, Filter => SFilter}
import javax.servlet.http.HttpServletRequest

import org.slf4j.LoggerFactory

import org.brzy.application.WebApp
import org.brzy.webapp.action.args.ArgsBuilder

/**
 * Forwards only requests to brzy actions, lets all other requests pass through.
 *
 * @author Michael Fortin
 */
class BrzyFilter extends SFilter {
  private[jetty] val log = LoggerFactory.getLogger(classOf[BrzyFilter])


  def init(fc: FilterConfig) {}

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
    val q = req.asInstanceOf[HttpServletRequest]
    log.trace("uri : {}", q.getRequestURI)
    val actionPath = ArgsBuilder.parseActionPath(q.getRequestURI, q.getContextPath)
    val webapp = q.getServletContext.getAttribute("application").asInstanceOf[WebApp]

    webapp.actions.find(_.path.isMatch(actionPath)) match {
      case Some(action) => // for action, don't continue
        doAction(webapp, q, res, chain)
      case _ => // pass it on if the url ends with any extension
        chain.doFilter(req, res)
    }
  }

  /**
   * TODO might want to change this so the transaction is only done within the forwarded call
   * and not wrapped around the entire dispatch.
   */
  private[this] def doAction(webapp:WebApp, req: HttpServletRequest, res: ServletResponse, chain: FilterChain) {
    val uri = req.getRequestURI
    val contextPath = req.asInstanceOf[HttpServletRequest].getContextPath

    val forward =
      if (contextPath == "")
        uri.substring(0, uri.length)
      else
        uri.substring(contextPath.length, uri.length)

    // the aop interceptors are run here so that the view rendering is also within the transaction
    log.trace("intercept: {}", forward)
    webapp.interceptor.doIn(() => {
      if (forward.endsWith(".brzy"))
        chain.doFilter(req, res)
      else
        req.getRequestDispatcher(forward + ".brzy").forward(req, res)
      None // the interceptor expects a return value
    })
  }

  /**
   *
   */
  def destroy() {
    log.trace("Destroy")
  }
}
