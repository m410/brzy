/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.webapp.view

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

  def resource(path:String,req:HttpServletRequest):String = 
      if(req.getContextPath == "/")
        path
      else
        req.getContextPath + path
  
  def res(path:String)(implicit req:HttpServletRequest):String = resource(path,req)
  
	def link(path:String)(implicit req:HttpServletRequest):String = resource(path,req)
  
  def action(path:String)(implicit req:HttpServletRequest):String = resource(path,req)
  
  def css(path:String)(implicit req:HttpServletRequest):String = resource( "/css" + path, req)
  
  def js(path:String)(implicit req:HttpServletRequest):String = resource("/js" +path, req)
  
  def img(path:String)(implicit req:HttpServletRequest):String = resource("/images" +path, req)
  
  def date(date:Date,format:String):String = new SimpleDateFormat(format).format(date)
  
  def number(num:Number,format:String):String = new DecimalFormat(format).format(num)
  
  def encode(path:String) = URLEncoder.encode(path, "UTF-8")  
  

  def flash()(implicit req:HttpServletRequest):String = {
    if(req.getSession.getAttribute("flash-message") != null)
      req.getSession.getAttribute("flash-message").asInstanceOf[FlashMessage].show
    else
      ""
  }

  def hasFlash()(implicit req:HttpServletRequest):Boolean = req.getSession.getAttribute("flash-message") != null
  
}