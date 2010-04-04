package org.brzy.action

import org.brzy.application.WebApp
import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}
import ActionSupport._
import org.slf4j.LoggerFactory

/**
 * The basic servlet implementation.
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class Servlet extends HttpServlet {
  private val log = LoggerFactory.getLogger(classOf[Servlet])

  override def service(req: HttpServletRequest, res: HttpServletResponse) = {
    val app = getServletContext.getAttribute("application").asInstanceOf[WebApp]
    val ctx = getServletContext

    if(!app.actions.exists(pathCompare(req.getRequestURI))) {
      res.sendError(404)
    }
    else {
      val action = app.actions.find(pathCompare(req.getRequestURI)).get
      log.debug("{} >> {}",req.getRequestURI, action)

      // TODO catch errors & throw 501?
      val args = buildArgs(action, req)
      val result = executeAction(action, args)
      handleResults(result,req,res,ctx)
    }
  }
}