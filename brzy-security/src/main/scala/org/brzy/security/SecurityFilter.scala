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
package org.brzy.security

import javax.servlet.{ServletResponse, ServletRequest, FilterConfig, Filter,FilterChain}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.slf4j.{LoggerFactory}
import org.brzy.application.WebApp
import org.brzy.mvc.action.Action._
import org.brzy.mvc.action.Action

/**
 *
 * @author Michael Fortin
 */
class SecurityFilter extends Filter{
  val log = LoggerFactory.getLogger(classOf[SecurityFilter])
  var security:SecurityModProvider = _
  var app:WebApp = _

  def init(filterConfig: FilterConfig) = {
    app = filterConfig.getServletContext.getAttribute("application").asInstanceOf[WebApp]
    val option = app.moduleResource.find(_.name == "brzy-security")
    security = option match {
      case Some(mod) => mod.asInstanceOf[SecurityModProvider]
      case _ => error("No Security Module Found")
    }
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) = {
    val request = req.asInstanceOf[HttpServletRequest]
    val response = res.asInstanceOf[HttpServletResponse]
    val actionPath = findActionPath(request.getRequestURI,request.getContextPath)
    log.debug("actionPath: " + actionPath)
    log.debug("s: 1")

    if(request.getSession.getAttribute("brzy_security_login") != null) {
      log.debug("s: 2")

      val actionOption = app.actions.find(pathCompare(actionPath))
      actionOption match {
        case Some(a) => secureAction(a,request,response,chain)
        case _ => chain.doFilter(request,response)
      }
    }
    else {
      log.debug("s: 3")

      request.getSession.setAttribute("brzy_security_page",request.getRequestURI)
      log.debug("security.defaultPath: {}",security.defaultPath )
      response.sendRedirect(request.getContextPath + security.defaultPath)
    }
  }

  def secureAction(a:Action,request:HttpServletRequest,response:HttpServletResponse,chain:FilterChain) = {
    log.debug("s: 4")

    val roles =
        if(request.getSession.getAttribute("brzy_security_roles") != null)
          request.getSession.getAttribute("brzy_security_roles").asInstanceOf[Array[String]]
        else
          Array[String]()

    val allowed =
        if(a.actionMethod.getAnnotation(classOf[Secured]) != null)
          a.actionMethod.getAnnotation(classOf[Secured]).value.asInstanceOf[Array[String]]
        else if(a.inst.getClass.getAnnotation(classOf[Secured]) != null)
          a.inst.getClass.getAnnotation(classOf[Secured]).value.asInstanceOf[Array[String]]
        else
          Array[String]()

    if(allowed.isEmpty || allowed.exists(allow => roles.exists(_ == allow)))
      chain.doFilter(request,response)
    else
      response.sendError(403,"Restricted")
  }

  def destroy = {
    // nothing to do
  }

}