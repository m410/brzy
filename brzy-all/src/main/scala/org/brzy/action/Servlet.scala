package org.brzy.action

import org.brzy.application.WebApp
import ActionSupport._
import org.slf4j.LoggerFactory
import javax.servlet.http._
import javax.servlet.{ServletResponse, ServletRequest}

/**
 * The basic servlet implementation.
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class Servlet extends HttpServlet {
  private val log = LoggerFactory.getLogger(classOf[Servlet])

  private def internal(req: HttpServletRequest, res: HttpServletResponse) = {
    val app = getServletContext.getAttribute("application").asInstanceOf[WebApp]
    val ctx = getServletContext
    log.trace("request:{}",req.getRequestURI)
    val actionPath = findActionPath(req.getRequestURI,req.getContextPath)

    if(!app.actions.exists(pathCompare(actionPath))) {
      res.sendError(404)
    }
    else {
      val action = app.actions.find(pathCompare(actionPath)).get
      log.debug("{} >> {}",req.getRequestURI, action)

      // TODO catch errors & throw 501?
      val args = buildArgs(action, req)
      val result = executeAction(action, args)
      handleResults(action,result,req,res)
    }
  }

  override def service(req: ServletRequest, res: ServletResponse) = {
    internal(req.asInstanceOf[HttpServletRequest],res.asInstanceOf[HttpServletResponse])
  }

  override def service(req: HttpServletRequest, res: HttpServletResponse) = {
    log.trace("service1")
    internal(req,res)
  }

  override def doDelete(req: HttpServletRequest, res: HttpServletResponse) = {
    log.trace("doDelete")
    internal(req,res)
  }

  override def doPut(req: HttpServletRequest, res: HttpServletResponse) = {
    log.trace("doPut")
    internal(req,res)
  }

  override def doPost(req: HttpServletRequest, res: HttpServletResponse) = {
    log.trace("doPost")
    internal(req,res)
  }

  override def doHead(req: HttpServletRequest, res: HttpServletResponse) = {
    log.trace("doHead")
    internal(req,res)
  }

  override def doGet(req: HttpServletRequest, res: HttpServletResponse) = {
    log.trace("doGet")
    internal(req,res)
  }

  override def doTrace(req: HttpServletRequest, res: HttpServletResponse) = {
    log.trace("doTrace")
    internal(req,res)
  }

  override def doOptions(req: HttpServletRequest, res: HttpServletResponse) = {
    log.trace("doOptions")
    internal(req,res)
  }
}