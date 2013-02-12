package org.brzy.webapp

import org.brzy.webapp.application.WebApp

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import javax.servlet.{ServletConfig, ServletResponse, ServletRequest}

import org.slf4j.LoggerFactory


/**
 * This executes async actions in an async servlet.
 * 
 * @author Michael Fortin
 */
class BrzyAsyncServlet extends HttpServlet {
  private val log = LoggerFactory.getLogger(classOf[BrzyServlet])

  override def init(config: ServletConfig) {
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    val request = req.asInstanceOf[HttpServletRequest]
    val response = res.asInstanceOf[HttpServletResponse]
    val webapp = req.getServletContext.getAttribute("application").asInstanceOf[WebApp]
    val action = webapp.serviceAction(request).getOrElse(throw new RuntimeException("error"))

    action.trans.doWith(webapp.threadLocalSessions,()=>{
      log.debug("async doService:{}",action)
      action.doService(request, response)
    })
  }
}
