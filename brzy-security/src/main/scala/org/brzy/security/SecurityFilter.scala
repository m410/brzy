package org.brzy.security

import javax.servlet.{ServletResponse, ServletRequest, FilterConfig, Filter,FilterChain}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}

import org.slf4j.{LoggerFactory}
import org.brzy.application.WebApp
import org.brzy.mvc.action.ActionSupport._
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