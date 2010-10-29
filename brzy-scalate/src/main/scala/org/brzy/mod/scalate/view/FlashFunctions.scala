package org.brzy.mod.scalate.view

import javax.servlet.http.HttpServletRequest
import org.brzy.webapp.view.FlashMessage

/**
 * Document Me..
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