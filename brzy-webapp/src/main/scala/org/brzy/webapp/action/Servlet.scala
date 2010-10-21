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

import org.brzy.application.WebApp
import Action._
import org.slf4j.LoggerFactory
import javax.servlet.http._
import javax.servlet.{ServletResponse, ServletRequest}

/**
 * The basic servlet implementation.
 * 
 * @author Michael Fortin
 */
class Servlet extends HttpServlet {
  private val log = LoggerFactory.getLogger(classOf[Servlet])

  private def internal(req: HttpServletRequest, res: HttpServletResponse) = {
    val app = getServletContext.getAttribute("application").asInstanceOf[WebApp]
    log.trace("request: {}",req.getRequestURI)
    val actionPath = findActionPath(req.getRequestURI,req.getContextPath)
    log.trace("path: {}",actionPath)
    
    if(!app.actions.exists(pathCompare(actionPath))) {
      res.sendError(404)
    }
    else {
      val action = app.actions.find(pathCompare(actionPath)).get
      log.debug("{} >> {}",req.getRequestURI, action)

      try {
        val args = buildArgs(action, req)
        val result = executeAction(action, args)
        handleResults(action,result,req,res)
      }
      catch {
        case unknown =>
          log.error("Exception: " + unknown,unknown)
          res.sendError(500)
      }
    }
  }

  override def service(req: ServletRequest, res: ServletResponse) = {
    internal(req.asInstanceOf[HttpServletRequest],res.asInstanceOf[HttpServletResponse])
  }

}