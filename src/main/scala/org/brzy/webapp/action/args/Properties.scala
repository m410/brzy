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

import java.util.Locale
import javax.servlet.http.HttpServletRequest
import collection.JavaConversions._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
trait Properties  extends Arg {
  def requestURI:String
  def requestURL:StringBuffer
  def queryString:String
  def scheme: String
  def secure: Boolean
  def serverName: String
  def serverPort: Int
  def remoteAddr: String
  def remoteHost: String
  def remotePort: Int
  def localAddr: String
  def localName: String
  def localPort: Int
  def contentLength: Int
  def contentType: String
  def characterEncoding: String
  def protocol: String
  def locale: Locale
  def locales: Array[Locale]
}

/**
 * Document me..
 */
class PropertiesRequest protected (request:HttpServletRequest) extends Properties {
  def requestURI = request.getRequestURI
  def requestURL = request.getRequestURL
  def queryString = request.getQueryString
  def scheme = request.getScheme
  def secure = request.isSecure
  def contentLength = request.getContentLength
  def contentType = request.getContentType
  def characterEncoding = request.getCharacterEncoding
  def protocol = request.getProtocol
  def serverName = request.getServerName
  def serverPort = request.getServerPort
  def remoteAddr = request.getRemoteAddr
  def remoteHost = request.getRemoteHost
  def remotePort = request.getRemotePort
  def localAddr = request.getLocalAddr
  def localName = request.getLocalName
  def localPort = request.getLocalPort
  def locale = request.getLocale
  def locales = request.getLocales.map(_.asInstanceOf[Locale]).toArray

  override def toString = new StringBuilder().append("PropertiesRequest[")
          .append("requestURI=").append(requestURI).append(", ")
          .append("requestURL=").append(requestURL).append(", ")
          .append("queryString=").append(queryString).append(", ")
          .append("scheme=").append(scheme).append(", ")
          .append("secure=").append(secure).append(", ")
          .append("contentLength=").append(contentLength).append(", ")
          .append("contentType=").append(contentType).append(", ")
          .append("characterEncoding=").append(characterEncoding).append(", ")
          .append("protocol=").append(protocol).append(", ")
          .append("serverName=").append(serverName).append(", ")
          .append("serverPort=").append(serverPort).append(", ")
          .append("remoteAddr=").append(remoteAddr).append(", ")
          .append("remoteHost=").append(remoteHost).append(", ")
          .append("remotePort=").append(remotePort).append(", ")
          .append("localAddr=").append(localAddr).append(", ")
          .append("localName=").append(localName).append(", ")
          .append("localPort=").append(localPort).append(", ")
          .append("locale=").append(locale).append(", ")
          .append("locales=").append(locales).append("]")
          .toString()
}