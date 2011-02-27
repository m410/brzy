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
package org.brzy.webapp.action.tmp

import xml.Elem
import com.twitter.json.{Json=>tJson}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
sealed trait Returns

/**
 *  Add a name value pair to the servlet request attributes.
 */
case class Model(attrs:Tuple2[String,AnyRef]*) extends Returns

/**
 * Add a cookie to the return headers.
 */
case class CookieAdd(attrs:Tuple2[String,AnyRef]) extends Returns

/**
 * add an attribute to the httpSession.
 */
case class SessionAdd(attrs:Tuple2[String,AnyRef]*) extends Returns

/**
 * Remove an attribute for the http session.
 */
case class SessionRemove(attr:String) extends Returns

/**
 * Add an attribute to the http session that is only available for a single
 * request by the client.
 */
case class Flash(code:String,default:String) extends Returns



trait Direction extends Returns


/**
 * Override the default view.
 */
case class View(path:String) extends Direction

/**
 * Forward to another action without sending a redirect to the client.
 */
case class Forward(path:String) extends Direction {
  val contextPath =
    if(path.startsWith("/"))
      path + ".brzy"
    else
      "/" + path + ".brzy"

}

/**
 * Send a 302 redirect to the cleint.
 */
case class Redirect(path:String) extends Direction

/**
 * Return xml as the body of the response.
 */
case class Xml(t:AnyRef,contentType:String = "text/xml") extends Direction with Parser {
  import org.brzy.fab.reflect.Properties._

  def parse = {
    def node(name:String, elem:Elem) = elem.copy(label=name)
    val tmp = <class>
      {t.properties.map(p=> node({p._1}, <property>{p._2}</property>))}
    </class>
    node(t.getClass.getSimpleName,tmp)
  }.toString
}

/**
 * Returns plain text as the body of the response.
 */
case class Text(ref:AnyRef, contentType:String = "text/plain") extends Direction with Parser{
  def parse = ref.toString
}

/**
 * Returns binary data as the body of the response.  This is used to return files or images
 * as the response.
 */
case class Binary(bytes:Array[Byte], contentType:String) extends Direction

/**
 * Return Json formatted text as the body of the response.
 */
case class Json(target:AnyRef, contentType:String = "application/json") extends Direction with Parser {

  def parse = target match {
    case s:String =>
      s
    case l:List[_] =>
      tJson.build(l).toString
    case m:Map[_,_] =>
      tJson.build(m).toString
    case _ =>
       import org.brzy.fab.reflect.Properties._
      tJson.build(target.properties).toString
  }
}

case class Jsonp(callback:String, target:AnyRef,contentType:String = "application/json") extends Direction with Parser {
  def parse = {
    val sb = new StringBuilder()
    sb.append(callback)
    sb.append("(")
    sb.append(target match {
      case s:String =>
        s
      case l:List[_] =>
        tJson.build(l).toString
      case m:Map[_,_] =>
        tJson.build(m).toString
      case _ =>
         import org.brzy.fab.reflect.Properties._
        tJson.build(target.properties).toString
    })
    sb.append(")")
    sb.toString
  }
}

/**
 * Return an error to the client, eg. 403.
 */
case class Error(code:Int, msg:String) extends Direction


