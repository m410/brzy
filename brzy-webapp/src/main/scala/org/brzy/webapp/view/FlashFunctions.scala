package org.brzy.webapp.view

import javax.servlet.http.HttpServletRequest

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

object FlashFunctions {

  def flash()(implicit req:HttpServletRequest):String = {
    if(req.getSession.getAttribute("flash-message") != null)
      req.getSession.getAttribute("flash-message").asInstanceOf[FlashMessage].show
    else
      ""
  }

  def hasFlash()(implicit req:HttpServletRequest):Boolean = req.getSession.getAttribute("flash-message") != null

}