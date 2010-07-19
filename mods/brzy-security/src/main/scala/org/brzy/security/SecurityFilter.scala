package org.brzy.security

import javax.servlet.{ServletResponse, ServletRequest, FilterConfig, Filter,FilterChain}
import org.slf4j.{LoggerFactory}
import org.brzy.application.WebApp
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.brzy.config.mod.ModResource

/**
 *
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
    // check if the session exists
    if(request.getSession.getAttribute("brzy_security_login") != null) {
      //  check action permissions
      //    yes =  proceed
      //      wrap request in new request with principal and roles
      chain.doFilter(request,response)
      //    no = 403
    }
    else {
      request.getSession.setAttribute("brzy_security_page",request.getRequestURI)
      log.debug("security.defaultPath: {}",security.defaultPath )
      response.sendRedirect(request.getContextPath + security.defaultPath)
    }
  }

  def findModule(r:HttpServletRequest):SecurityModResource = {
    val app = r.getSession.getServletContext.getAttribute("application").asInstanceOf[WebApp]
    val option = app.moduleResource.find(_.name == "brzy-security")
    option match {
      case Some(mod) => mod.asInstanceOf[SecurityModResource]
      case _ => error("No Security Module Found")
    }
  }
  def destroy = {
    // nothing to do
  }

}