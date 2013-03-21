package org.brzy.webapp

import action.Action
import javax.servlet.http.HttpServletRequest


/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
sealed trait FilterDirect

case class ActOn(action:Action) extends FilterDirect

case class ActOnAsync(action:Action) extends FilterDirect

case class RedirectToSecure(path:String) extends FilterDirect

object RedirectToSecure {
  def apply(request:HttpServletRequest) = {
    val url = request.getRequestURL.replace(0, 4, "https")
    new RedirectToSecure(url.toString)
  }
}

case class RedirectToAuthenticate(path:String, lastView:String = "") extends FilterDirect

case class DispatchTo(path:String) extends FilterDirect

case object NotAnAction  extends FilterDirect

case object Forbidden extends FilterDirect
