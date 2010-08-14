package org.brzy.mvc.action

import org.brzy.application.WebApp
import ActionSupport._
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
          log.error("Exception:" + unknown,unknown)
          res.sendError(500)
      }
    }
  }

  override def service(req: ServletRequest, res: ServletResponse) = {
    internal(req.asInstanceOf[HttpServletRequest],res.asInstanceOf[HttpServletResponse])
  }

}