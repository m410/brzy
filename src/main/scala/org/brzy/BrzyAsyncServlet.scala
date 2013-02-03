package org.brzy

import action.response.Async
import org.brzy.application.WebApp

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
  protected var webapp: WebApp = _

  override def init(config: ServletConfig) {
    webapp = config.getServletContext.getAttribute("application").asInstanceOf[WebApp]
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    val request = req.asInstanceOf[HttpServletRequest]
    val response = req.asInstanceOf[HttpServletResponse]
    val asyncContext = request.startAsync()
    val action = webapp.serviceAction(request).getOrElse(throw new RuntimeException("error"))

    val async = action.trans.doWithResult(webapp.threadLocalSessions,()=>{
      action.doService(request, response).asInstanceOf[Async]
    }).asInstanceOf[Async]

    asyncContext.addListener(async.listener)
    asyncContext.start(async.start(webapp.threadLocalSessions,action.trans,asyncContext))
  }
}
