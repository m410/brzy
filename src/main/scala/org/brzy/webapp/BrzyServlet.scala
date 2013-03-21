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

import org.brzy.webapp.application.WebApp


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

  override def init(config: ServletConfig) {
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    val request = req.asInstanceOf[HttpServletRequest]
    val response = res.asInstanceOf[HttpServletResponse]
    val webapp = req.getServletContext.getAttribute("application").asInstanceOf[WebApp]

    webapp.serviceAction(request)
            .getOrElse(throw new RuntimeException(s"Action not found: ${request.getRequestURI}"))
            .doService(request, response)
  }
}