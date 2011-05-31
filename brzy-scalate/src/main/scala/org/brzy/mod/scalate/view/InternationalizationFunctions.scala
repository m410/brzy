package org.brzy.mod.scalate.view

import javax.servlet.http.HttpServletRequest

/**
 * Provides internationalization support to views.
 * 
 * @author Michael Fortin
 */
object InternationalizationFunctions {
  def i18n(message:String)(implicit req:HttpServletRequest) = {
    MessageResolver.message(message,req.getLocale)
  }
}