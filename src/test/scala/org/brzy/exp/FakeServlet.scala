package org.brzy.exp

import javax.servlet.http.HttpServletRequest

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

  def filterChain(request:HttpServletRequest) {
    application.actionFor(request)  match {
      case Some(action) =>
        val tlocal = application.localThreadSessions
        action.transaction.doIn(tlocal, {()=>
          //chain.doFilter(a,b)
        })
      case None =>
    }
  }
}
