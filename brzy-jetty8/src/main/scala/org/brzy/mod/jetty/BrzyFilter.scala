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
package org.brzy.mod.jetty

import javax.servlet.{FilterChain, FilterConfig, ServletResponse, ServletRequest, Filter => SFilter}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.slf4j.LoggerFactory
import java.lang.reflect.Method

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
    val webapp = q.getServletContext.getAttribute("application")
    log.trace("webapp: {}", webapp)

    val path = isPathMethod(webapp)
    val trans = wrapTransMethod(webapp)

    if (path.invoke(webapp, q.getContextPath, q.getRequestURI).asInstanceOf[Boolean])
      trans.invoke(webapp, req, res, chain)
    else
      chain.doFilter(req, res)

  }


  private[this] def wrapTransMethod(webapp: AnyRef): Method = {
    webapp.getClass.getMethod("wrapWithTransaction", classOf[HttpServletRequest], classOf[HttpServletResponse], classOf[FilterChain])
  }

  private[this] def isPathMethod(webapp: AnyRef): Method = {
    webapp.getClass.getMethod("isPath", classOf[String], classOf[String])
  }

  /**
   *
   */
  def destroy() {
    log.trace("Destroy")
  }
}
