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
package org.brzy.webapp.action.args

import javax.servlet.http.HttpServletRequest
import collection.JavaConversions._

/**
 * this is not in use, but a template of how action arguments should be done.  The
 * arguments should all be traits instead of case classes as to facilitate easier unit
 * testing.  Unit testing should not depend on the use of the http servlet request object.  This
 * would also combine several args objects into one.
 */
trait Attributes extends Arg {
  def apply(name:String):AnyRef

  def url:Map[String, String]

  def request:Map[String, Array[String]]

  def application:Map[String, AnyRef]

  def header:Map[String, String]

  def session:Option[Map[String, AnyRef]]
}


class AttributeRequest(req:HttpServletRequest, urlParams:Map[String, String]) extends Attributes {

  def apply(name: String) = {
    if (urlParams.containsKey(name))
      urlParams(name)
    else if(req.getParameter(name) != null)
      req.getParameter(name)
    else if (req.getSession(false) != null && req.getSession.getAttribute(name) != null)
      req.getSession.getAttribute(name)
    else if (req.getSession.getServletContext.getAttribute(name) != null)
      req.getSession.getServletContext.getAttribute(name)
    else if (req.getHeader(name) != null)
      req.getHeader(name)
    else
      error("No Parameter with name '"+name+"' in any scope")
  }

  def url = urlParams

  def request = req.getParameterMap.map({case (n:String, v:Array[String]) => n->v}).toMap

  def application = {
    val ctx = req.getSession.getServletContext
    ctx.getAttributeNames.map( { case (n:String)=> n->req.getAttribute(n)}).toMap
  }

  def header = req.getHeaderNames.map({case (n:String)=> n->req.getHeader(n)}).toMap

  def session = if (req.getSession(false) != null)
      Option(req.getSession.getAttributeNames.map({case (n:String)=> n->req.getAttribute(n)}).toMap)
    else
      None
}