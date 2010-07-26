package org.brzy.view

import javax.servlet.http.HttpServletRequest
import java.util.Date
import java.text.{DecimalFormat, SimpleDateFormat}
import java.net.URLEncoder

/**
 * @author Michael Fortin
 * @version $Id: $
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
}