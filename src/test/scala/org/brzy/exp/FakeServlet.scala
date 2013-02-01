package org.brzy.exp

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import javax.servlet.{FilterChain, ServletResponse, ServletRequest}

/**
 * in filter
 * application.actionFor(request) match {
 *   case Some(a) =>
 *     if(isInternalPath(request))
 *      a.transaction.doIn {()=> chain.doFilter(req,res) }
 *     else
 *      req.getRequestDispatcher(forward + ".brzy").forward(req, res)
 *   case _ =>
 *     chain.doFilter(req,res)
 *  }
 *
 *  application.actionFor(request) match {
 *   case Some(a) =>
 *   if(a.isSecure(request))
 *   if(a.requiredAuthentication(request) && a.authenticated(request))
 *    if(a.isAuthorized(request))
 *      val result = a.execute(request,response)
          ResponseHandler(action, result, request, response)
 *    else
 *   case _ =>
 *  }
 *
 * @author Michael Fortin
 */
class FakeServlet {

  val application:Application = new Application(null)

  def doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
    application.doFilterAction(request)  match {
      case ActOn(action) =>
        val tlocal = application.threadLocalSessions
        action.transaction.doIn(tlocal, {()=>
          chain.doFilter(req,res)
        })
      case ActOnAsync(action) =>
        chain.doFilter(req,res)
      case RedirectToSecure(path) =>
        res.sendRedirect(path)
      case RedirectToAuthenticate(path)=>
        res.sendRedirect(path)
      case DispatchTo(path) =>
        req.getRequestDispatcher(s"$path.brzy").forward(req, res)
      case NotAnAction =>
        chain.doFilter(req,res)
    }
  }

  override def service(req: ServletRequest, res: ServletResponse) {
    val action = application.doServiceAction(request).getOrElse(throw new RuntimeException("error"))
    action.doService(request, response)
  }
}
