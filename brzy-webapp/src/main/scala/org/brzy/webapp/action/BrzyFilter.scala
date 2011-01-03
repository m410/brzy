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

import javax.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import javax.servlet.{FilterChain, FilterConfig, ServletResponse, ServletRequest, Filter => SFilter}
import org.brzy.application.WebApp

/**
 * Forwards only requests to brzy actions, lets all other pass through.
 *
 * @author Michael Fortin
 */
class BrzyFilter extends SFilter {
  protected[action] val log = LoggerFactory.getLogger(classOf[BrzyFilter])
  protected[action] val pattern = """\.([\w\d]{1,4})$""".r
  protected[action] var webapp:WebApp = _

  /**
   *
   */
  def init(config: FilterConfig) = {
    log.info("Init Filter: {}", config)
    webapp = config.getServletContext.getAttribute("application").asInstanceOf[WebApp]
  }

  /**
   *
   */
  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) = {
    val uri = req.asInstanceOf[HttpServletRequest].getRequestURI
    log.trace("uri    : {}",uri)
    
    if(!(pattern findFirstMatchIn uri).isEmpty) { // pass it on if the url ends with any extension
      chain.doFilter(req,res)
    }
    else { // assume it's an action and append .brzy
      val contextPath = req.asInstanceOf[HttpServletRequest].getContextPath
      val forward =
        if(contextPath == "")
          uri.substring(0,uri.length)
        else
          uri.substring(contextPath.length,uri.length)

      // the aop interceptors are run here so that the view rendering is also within the transaction
      log.trace("forward: {}",forward)
      webapp.interceptor.doIn(() => {
        req.getRequestDispatcher(forward + ".brzy").forward(req,res)
        null
      }) 
    }
  }

  /**
   *
   */
  def destroy = {
    log.debug("Destroy")
  }
}