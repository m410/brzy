package org.brzy.mvc.view

import javax.servlet.http.HttpServletRequest
import java.util.Date
import java.text.{DecimalFormat, SimpleDateFormat}
import java.net.URLEncoder

/**
 * TODO, this belongs with the scalate module.
 *
 * @author Michael Fortin
 */
object ViewFunctions {

  def resource(path:String,request:HttpServletRequest):String = {
    if(request.getContextPath == "/")
      path
    else
      request.getContextPath + path
  }

  def res(path:String)(implicit request:HttpServletRequest):String = {
    request.getContextPath + path
  }

	def link(path:String)(implicit request:HttpServletRequest):String = {
    request.getContextPath + path
  }

  def action(path:String)(implicit request:HttpServletRequest):String = {
    request.getContextPath + path
  }

  def css(path:String)(implicit request:HttpServletRequest):String = {
    resource( "/css" + path, request)
  }

  def js(path:String)(implicit request:HttpServletRequest):String = {
    resource("/js" +path, request)
  }

  def img(path:String)(implicit request:HttpServletRequest):String = {
    resource("/images" +path, request)
  }

  def date(date:Date,format:String):String = {
    new SimpleDateFormat(format).format(date)
  }

  def number(num:Number,format:String):String = {
    new DecimalFormat(format).format(num)
  }

  def encode(path:String) = {
    URLEncoder.encode(path, "UTF-8")  
  }

  def flash()(implicit request:HttpServletRequest):String = {
    if(request.getSession.getAttribute("flash-message") != null)
      request.getSession.getAttribute("flash-message").asInstanceOf[FlashMessage].show
    else
      ""
  }

  def hasFlash()(implicit request:HttpServletRequest):Boolean = {
    request.getSession.getAttribute("flash-message") != null
  }
}