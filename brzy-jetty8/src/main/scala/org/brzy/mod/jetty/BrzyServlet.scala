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

import javax.servlet.ServletConfig
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

import org.slf4j.LoggerFactory
import actors.Future

import java.io.File
import java.lang.reflect.{InvocationTargetException, Method}


/**
 * Accepts requests to the application like the Brzy servlet, but checks for changes in
 * the source and recompiles and reloads the application.
 *
 * @author Michael Fortin
 */
class BrzyServlet extends HttpServlet {
  private[this] val log = LoggerFactory.getLogger(classOf[BrzyServlet])

  override def init(config: ServletConfig) {
  }

  override def service(req: HttpServletRequest, resp: HttpServletResponse) {
    internal(req, resp)
  }

  private[this] def internal(req: HttpServletRequest, res: HttpServletResponse) {
    log.debug("request: {}, context: {}", req.getServletPath, req.getContextPath)
    val webApp = req.getServletContext.getAttribute("application")

    try {
      callActionMethod(webApp).invoke(webApp, req, res)
    }
    catch {
      case i: InvocationTargetException => throw i.getCause
    }
  }

  private[this] def callActionMethod(wa: AnyRef): Method = {
      wa.getClass.getMethod("callAction", classOf[HttpServletRequest], classOf[HttpServletResponse])
  }
}
