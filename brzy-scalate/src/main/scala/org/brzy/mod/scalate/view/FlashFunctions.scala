package org.brzy.mod.scalate.view

import javax.servlet.http.HttpServletRequest
import org.brzy.webapp.view.FlashMessage

/**
 * Scalate built in view functions.  This provides a simple way to view flash messages.
 * 
 * @author Michael Fortin
 */
object FlashFunctions {

  def flash()(implicit req:HttpServletRequest):String = {
    if(req.getSession.getAttribute("flash-message") != null)
      req.getSession.getAttribute("flash-message").asInstanceOf[FlashMessage].show
    else
      ""
  }

  def hasFlash()(implicit req:HttpServletRequest):Boolean = {
    req.getSession.getAttribute("flash-message") != null
  }
    
}