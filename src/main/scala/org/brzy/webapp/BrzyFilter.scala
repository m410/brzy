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

import action.args.ArgsBuilder
import javax.servlet.{FilterChain, FilterConfig, ServletResponse, ServletRequest, Filter => SFilter}
import javax.servlet.http.HttpServletRequest

import org.slf4j.LoggerFactory

import org.brzy.application.WebApp

/**
 * Forwards only requests to brzy actions, lets all other requests pass through.
 *
 * @author Michael Fortin
 */
class BrzyFilter extends SFilter {
  private[this] val log = LoggerFactory.getLogger(classOf[BrzyFilter])
  protected[webapp] var webapp: WebApp = _

  /**
   *
   */
  def init(config: FilterConfig) {
    log.debug("Init Filter: {}", config)
    webapp = config.getServletContext.getAttribute("application").asInstanceOf[WebApp]
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
    val q = req.asInstanceOf[HttpServletRequest]
    log.trace("uri : {}", q.getRequestURI)
    val actionPath = ArgsBuilder.parseActionPath(q.getRequestURI, q.getContextPath)

    webapp.actions.find(_.path.isMatch(actionPath)) match {
      case Some(action) => // for action, don't continue
        try { 
					doAction(q.getRequestURI, req, res,chain)
				}
				catch {
				  case e: Throwable =>
            log.error(e.getMessage,e)
            throw e
				}
      case _ => // pass it on if the url ends with any extension
        chain.doFilter(req, res)
    }
  }

  /**
   * TODO might want to change this so the transaction is only done within the forwarded call
   * and not wrapped around the entire dispatch.
   */
  private[this] def doAction(uri: String, req: ServletRequest, res: ServletResponse, chain: FilterChain) {
    val contextPath = req.asInstanceOf[HttpServletRequest].getContextPath

    val forward =
      if (contextPath == "")
        uri.substring(0, uri.length)
      else
        uri.substring(contextPath.length, uri.length)

    // todo get the transaction from the action, wrap call in it, if it's session in view

    // the aop interceptors are run here so that the view rendering is also within the transaction
    log.trace("intercept: {}", forward)
    webapp.interceptor.doIn(() => {
      if (forward.endsWith(".brzy"))
        chain.doFilter(req,res)
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
