package org.brzy.mod.scalate.view

import javax.servlet.http.HttpServletRequest
import java.util.Date
import java.text.{SimpleDateFormat, DecimalFormat}
import java.net.URLEncoder
import org.brzy.webapp.view.FlashMessage

/**
 * Helper view functions for links, number and date formatting.
 * 
 * @author Michael Fortin
 */
object UrlFunctions {

  def resource(path:String,req:HttpServletRequest):String = {
    val out = if(req.getContextPath == "/")
        path
      else
        req.getContextPath + path

    if(req.getSession(false) != null && !req.isRequestedSessionIdFromCookie) {
        // todo url rewrite
    }
    out
  }

  def res(path:String)(implicit req:HttpServletRequest):String = resource(path,req)

	def link(path:String)(implicit req:HttpServletRequest):String = resource(path,req)

  def action(path:String)(implicit req:HttpServletRequest):String = resource(path,req)

  def css(path:String)(implicit req:HttpServletRequest):String = resource( "_/css" + path, req)

  def js(path:String)(implicit req:HttpServletRequest):String = resource("_/js" +path, req)

  def img(path:String)(implicit req:HttpServletRequest):String = resource("_/images" +path, req)

  def date(date:Date,format:String):String = new SimpleDateFormat(format).format(date)

  def number(num:Number,format:String):String = new DecimalFormat(format).format(num)

  def encode(path:String) = URLEncoder.encode(path, "UTF-8")
}