package org.brzy.security

import javax.servlet.{ServletResponse, ServletRequest, FilterConfig, Filter,FilterChain}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.slf4j.{LoggerFactory}
import org.brzy.application.WebApp
import org.brzy.action.ActionSupport._
import org.brzy.action.Action

/**
 *
 * @author Michael Fortin
 */
class SecurityFilter extends Filter{
  val log = LoggerFactory.getLogger(classOf[SecurityFilter])


  def init(filterConfig: FilterConfig) = {
  }

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) = {
    val request = req.asInstanceOf[HttpServletRequest]
    val response = res.asInstanceOf[HttpServletResponse]
    val security = findModule(request)
    val app = findApp(request)
    val actionPath = findActionPath(request.getRequestURI,request.getContextPath)

    if(request.getSession.getAttribute("brzy_security_login") != null) {
      app.actions.find(pathCompare(actionPath)) match {
        case Some(a) => secureAction(a,request,chain)
        case _ => chain.doFilter(request,response)
      }
    }
    else {
      request.getSession.setAttribute("brzy_security_page",request.getRequestURI)
      log.debug("security.defaultPath: {}",security.defaultPath )
      response.sendRedirect(request.getContextPath + security.defaultPath)
    }
  }

  def secureAction(a:Action,request:HttpServletRequest,chain:FilterChain) = {
    // get the user name and the roles from the database
  }

  def findModule(r:HttpServletRequest):SecurityModResource = {
    val app = r.getSession.getServletContext.getAttribute("application").asInstanceOf[WebApp]
    val option = app.moduleResource.find(_.name == "brzy-security")
    option match {
      case Some(mod) => mod.asInstanceOf[SecurityModResource]
      case _ => error("No Security Module Found")
    }
  }

  def findApp(r:HttpServletRequest):WebApp = {
      r.getSession.getServletContext.getAttribute("application").asInstanceOf[WebApp]
  }

  def destroy = {
    // nothing to do
  }

}