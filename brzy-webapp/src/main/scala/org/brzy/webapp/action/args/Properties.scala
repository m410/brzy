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

class PropertiesRequest(request:HttpServletRequest) extends Properties {
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
}