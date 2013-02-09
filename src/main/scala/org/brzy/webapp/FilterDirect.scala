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
    val buf = request.getRequestURL
    val redirect = buf.replace(0, 4, "https").replace(buf.length() - 5, buf.length(),"").toString
    new RedirectToSecure(redirect)
  }
}

case class RedirectToAuthenticate(path:String) extends FilterDirect

case class DispatchTo(path:String) extends FilterDirect

case object NotAnAction  extends FilterDirect
