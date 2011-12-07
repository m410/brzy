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
package org.brzy.webapp.action.response

import org.scalastuff.scalabeans.Preamble._
import xml.Elem
import java.io.OutputStream
import com.twitter.json.{Json=>tJson}
import org.brzy.webapp.action.Parser

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
sealed trait Direction extends Response


/**
 * Override the default view.
 */
case class View(path: String) extends Direction

/**
 * Forward to another action without sending a redirect to the client.
 */
case class Forward(path: String) extends Direction {
  val contextPath =
    if (path.startsWith("/"))
      path + ".brzy"
    else
      "/" + path + ".brzy"

}

/**
 * Send a 302 redirect to the cleint.
 */
case class Redirect(path: String) extends Direction

/**
 * Return xml as the body of the response.
 */
case class Xml[T<:AnyRef:Manifest](t: T, contentType: String = "text/xml") extends Direction with Parser {

  def parse = {
    val descriptor = descriptorOf[T]
    val map = descriptor.properties.map(p=>{
      p.name -> descriptor.get(t,p.name)
    }).toMap
    def node(name: String, elem: Elem) = elem.copy(label = name)
    val tmp = <class>
      {map.map(p => node({p._1}, <property>{p._2}</property>))}
    </class>
    node(t.getClass.getSimpleName, tmp)
  }.toString()
}

/**
 * Returns plain text as the body of the response.
 */
case class Text(ref: AnyRef, contentType: String = "text/plain") extends Direction with Parser {
  def parse = ref.toString
}

/**
 * Returns binary data as the body of the response.  This is used to return files or images
 * as the response.
 */
case class Binary(bytes: Array[Byte], contentType: String) extends Direction

/**
 * Write to the response output stream.
 *
 * @param io The response output stream on loan.
 * @param contentType the contentType header to set for the response.
 */
case class Stream(io: (OutputStream)=>Unit, contentType: String) extends Direction

/**
 * Return Json formatted text as the body of the response.  If you need to override how the target
 * entity gets serialized mix-in the Parser trait.
 * {{{
 * new Json(target) with Parser {
 *   override def parse = {
 *    // custom parser here
 *    ""
 *   }
 * }}}
 *
 * @param target The object to serialize into json
 * @param contentType the content type header value to set.
 */
case class Json[T<:AnyRef:Manifest](target: T, contentType: String = "application/json") extends Direction with Parser {

  def parse = target match {
    case s: String =>
      s
    case l: List[_] =>
      tJson.build(l).toString()
    case m: Map[_, _] =>
      tJson.build(m).toString()
    case _ =>
      val descriptor = descriptorOf[T]
      val map = descriptor.properties.map(p=>{
        p.name -> descriptor.get(target,p.name)
      }).toMap
      tJson.build(map).toString()
  }
}

/**
 * Just like the json response, but wraps the json body with a javascript method call.
 *
 * @param callback The name of the javascript callback.
 * @param target The target object to serialize into json.
 * @param contentType Defaults to application/json, but can be overriden.
 */
case class Jsonp[T<:AnyRef:Manifest](callback: String, target: T, contentType: String = "application/json") extends Direction with Parser {
  def parse = {
    val sb = new StringBuilder()
    sb.append(callback)
    sb.append("(")
    sb.append(target match {
      case s: String =>
        s
      case l: List[_] =>
        tJson.build(l).toString()
      case m: Map[_, _] =>
        tJson.build(m).toString()
      case _ =>
        val descriptor = descriptorOf[T]
        val map = descriptor.properties.map(p=>{
          p.name -> descriptor.get(target,p.name)
        }).toMap
        tJson.build(map).toString()
    })
    sb.append(")")
    sb.toString()
  }
}

/**
 * Return an error to the client, eg. 403.
 */
case class Error(code: Int, msg: String) extends Direction