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

import javax.servlet.http.HttpServletRequest
import collection.JavaConversions._
import collection.immutable._


import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.FileItem

import io.Source
import xml.XML
import com.twitter.json.{Json=>tJson}

import org.brzy.webapp.action.Roles

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
sealed trait Args

/**
 *
 */
case class Protocol(value: String) extends Args

/**
 *
 */
case class ContentType(value: String) extends Args

/**
 *
 */
case class RemoteHost(value: String) extends Args

/**
 *
 */
case class RemoteAddr(value: String) extends Args

/**
 *
 */
case class Schema(value: String) extends Args

/**
 *
 */
case class IsSecure(value: Boolean) extends Args

/**
 *
 */
case class ServerName(value: String) extends Args

/**
 *
 */
case class ServerPort(value: String) extends Args

/**
 * A Action argument class to read header information.  Use it like
 * <pre>def myaction(c:Headers) = {
 *    val headerValue = c("name")
 * }
 * </pre>
 */
case class Headers(internalMap: Map[String, String]) extends Map[String, String] with MapLike[String, String, Headers] with Args {

  override def empty: Headers = new Headers(Map.empty[String, String])

  def +[B1 >: String](kv: (String, B1)) = null

  def -(key: String) = null

  def iterator = internalMap.iterator

  def get(key: String) = internalMap.get(key)
}

/**
 *
 */
object Headers {

  def apply(request: HttpServletRequest) = {
    val map = collection.mutable.Map[String, String]()

    if (request != null)
      request.getHeaderNames.foreach(f => {
        val str = f.asInstanceOf[String]
        map += str -> request.getHeader(str).asInstanceOf[String]
      })

    new Headers(map.toMap)
  }
}

/**
 *
 */
@serializable
case class Principal(name: String, roles: Roles)

/**
 *
 */
case class Session extends HashMap[String, Any] with Args

/**
 *
 */
case class Parameters(map: collection.Map[String, Array[String]])
        extends Map[String, String] with MapLike[String, String, Parameters] with Args {

  override def empty: Parameters = new Parameters(Map[String, Array[String]]())

  override def get(key: String): Option[String] =
    if (map.contains(key))
      Option(map(key)(0))
    else
      None

  def array(key: String): Option[Array[String]] = map.get(key)

  override def iterator: Iterator[(String, String)] = Iterator(map.map(f => f._1 -> f._2(0)).toArray: _*)

  // does nothing
  override def +[B1 >: String](kv: (String, B1)): Parameters = this

  // does nothing
  override def -(key: String): Parameters = this
}

/**
 * A Action argument class to read cookie information.  Use it like
 * <pre>def myaction(c:Cookies) = {
 *    val cookie = c.cookies.find(_.name == "").get
 * }
 * </pre>
 */
case class Cookies(list: List[Cookie]) extends Args

/**
 *
 */
object Cookies {
  def apply(request: HttpServletRequest) {
    val cookies = {
      if (request.getCookies == null || request.getCookies.length == 0)
        List.empty[Cookie]
      else
        request.getCookies.map(c => {
          Cookie(c.getComment, c.getDomain, c.getMaxAge, c.getName, c.getPath, c.getSecure, c.getValue, c.getVersion)
        }).toList
    }
    new Cookies(cookies)
  }
}

/**
 *
 */
case class Cookie(comment: String, domain: String, maxAge: Int, name: String,
        path: String, secure: Boolean, value: String, version: Int)

/**
 * This is not implemented yet.  This enables access to the the body of the post as a plain
 * text object to be used in parsing text, xml or json post data.
 *
 * @see http ://commons.apache.org/fileupload/using.html
 * @author Michael Fortin
 */
case class PostBody(request: HttpServletRequest) extends Args {
  val maxSize = 100000
  val tempDir = new java.io.File(util.Properties.tmpDir)
  val sizeThreshold = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD

  def asText = Source.fromInputStream(request.getInputStream).mkString

  def asXml = XML.load(request.getInputStream)

  def asJson = tJson.parse(asText)

  def asBytes(name: String): Array[Byte] = {
    val factory = new DiskFileItemFactory()
    factory.setSizeThreshold(sizeThreshold)
    factory.setRepository(tempDir)
    val upload = new ServletFileUpload(factory)
    upload.setSizeMax(maxSize)
    val items = upload.parseRequest(request)

    val item = items.find(x => {
      val i = x.asInstanceOf[FileItem]
      i.isFormField && i.getFieldName == name
    })

    item match {
      case Some(i) => i.asInstanceOf[FileItem].get
      case _ => error("Not a byte array.")
    }
  }
}
