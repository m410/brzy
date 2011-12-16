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
 * testing.  Unit testing should not depend on the use of the http servlet request
 * object.  This would also combine several args objects into one.
 */
trait Parameters extends Arg {

  /**
   * just request paramters and url parameters
   */
  def apply(name:String):String

  /**
   * all scopes
   */
  def get(name:String):Option[AnyRef]

  def requestAndUrl:Map[String, String]

  /**
   * parameters embedded in the url for RESTful access.
   */
  def url:Map[String, String]

  /**
   * the servlet request attributes
   */
  def request:Map[String, List[String]]

  /**
   * The application scope attributes.  servlet 5 spec, uses the session scope.
   */
  def application:Map[String, AnyRef]

  /**
   * Request headers
   */
  def header:Map[String, String]

  /**
   * the optional session scope attributes
   */
  def session:Option[Map[String, AnyRef]]
}

/**
 * Default implementation passed as the argument to the actions.
 */
class ParametersRequest protected (req:HttpServletRequest, urlParams:Map[String, String]) extends Parameters {

  
  def get(name: String) = {
    if (urlParams.contains(name))
      Option(urlParams(name))
    else if(req.getParameter(name) != null)
      Option(req.getParameter(name))
    else if (req.getSession(false) != null && req.getSession.getAttribute(name) != null)
      Option(req.getSession.getAttribute(name))
    else if (req.getSession.getServletContext.getAttribute(name) != null)
      Option(req.getSession.getServletContext.getAttribute(name))
    else if (req.getHeader(name) != null)
      Option(req.getHeader(name))
    else
      None
  }


  def requestAndUrl = urlParams ++ req.getParameterNames.map({
      case (n:String)=> n->req.getParameter(n)
    }).toMap

  def apply(name:String) = {
    if (urlParams.contains(name))
      urlParams(name)
    else if (req.getParameter(name) != null)
      req.getParameter(name)
    else
      throw new UnfoundParameterException("No Parameter with name '"+name+"' in request or url scope.")
  }

  val url = urlParams

  lazy val request = req.getParameterNames.map({
    case (n:String)=> n->req.getParameterValues(n).toList
  }).toMap

  lazy val application = {
    req.getSession.getServletContext.getAttributeNames.map( {
      case (n:String)=> n->req.getSession.getServletContext.getAttribute(n)
    }).toMap
  }

  lazy val header = req.getHeaderNames.map({
    case (n:String)=> n->req.getHeader(n)
  }).toMap

  lazy val session = if (req.getSession(false) != null)
      Option(req.getSession.getAttributeNames.map({
        case (n:String)=> n->req.getSession.getAttribute(n)
      }).toMap)
    else
      None

  override def toString = {
    val buf = new StringBuilder()
            .append("Parameters(")
            .append("url=").append(url.mkString("[", ", ", "]"))
            .append(",request=").append(request.map(a => a._1 -> a._2.toSeq).mkString("[", ", ", "]"))
    session match {
      case Some(s) => buf.append(",session=").append(s.mkString("[", ", ", "]"))
      case _ => buf.append(",session=<None>")
    }
    buf.append(",header=").append(header.mkString("[", ", ", "]"))
            .append(",application=").append(application.mkString("[", ", ", "]"))
            .append(")")
    buf.toString()
  }
}