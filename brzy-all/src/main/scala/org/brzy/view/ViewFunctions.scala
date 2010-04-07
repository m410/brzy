package org.brzy.view

import javax.servlet.http.HttpServletRequest
import java.util.Date
import java.text.{DecimalFormat, SimpleDateFormat, DateFormat}
import java.net.URLEncoder

/**
 * @author Michael Fortin
 * @version $Id: $
 */
object ViewFunctions {

  def resource(path:String,request:HttpServletRequest):String = {
    request.getContextPath + path
  }

  def res(path:String)(implicit request:HttpServletRequest):String = {
    request.getContextPath + path
  }

  def action(path:String,request:HttpServletRequest):String = {
    request.getContextPath + path
  }

  def date(date:Date,format:String):String = {
    new SimpleDateFormat(format).format(date)
  }

  def number(num:Number,format:String):String = {
    new DecimalFormat(format).format(num)
  }

  def html(str:String):String = {
    str.replaceAll("'","&#39;")
       .replaceAll("\"","&quot;")
       .replaceAll("&\\s","&amp; ")
       .replaceAll("<","&lt;")
       .replaceAll(">","&gt;") 
  }

  def encode(path:String) = {
    URLEncoder.encode(path, "UTF-8")  
  }

  def css(path:String,request:HttpServletRequest):String = {
    resource( "/css" + path, request)
  }

  def js(path:String,request:HttpServletRequest):String = {
    resource("/js" +path, request)
  }
}