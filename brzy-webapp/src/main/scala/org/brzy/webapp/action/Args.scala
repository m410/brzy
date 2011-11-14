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
package org.brzy.webapp.action

import javax.servlet.http.HttpServletRequest
import collection.JavaConversions._
import java.util.Locale

import org.apache.commons.fileupload.disk.DiskFileItemFactory
import org.apache.commons.fileupload.servlet.ServletFileUpload
import org.apache.commons.fileupload.FileItem

import io.Source
import xml.XML
import com.twitter.json.{Json => tJson}
import org.slf4j.LoggerFactory


/**
 * These are the eligable arguments to a controller action.
 *
 * @author Michael Fortin
 */
sealed class Args

/**
 *  Captures the request attributes and passes them on to the action.
 */
case class RequestAttributes(
				requestURI:String,
				requestURL:StringBuffer,
				queryString:String,
        scheme: String,
        secure: Boolean,
        serverName: String,
        serverPort: Int,
        remoteAddr: String,
        remoteHost: String,
        remotePort: Int,
        localAddr: String,
        localName: String,
        localPort: Int,
        contentLength: Int,
        contentType: String,
        characterEncoding: String,
        protocol: String,
        locale: Locale,
        locales: Array[Locale]) extends Args

/**
 *	Constructs a RequestAttribute class from the servlet request.
 */
object RequestAttributes {
  def apply(request: HttpServletRequest) =
    new RequestAttributes(
			requestURI = request.getRequestURI,
			requestURL = request.getRequestURL,
			queryString = request.getQueryString,
      scheme = request.getScheme,
      secure = request.isSecure,
      contentLength = request.getContentLength,
      contentType = request.getContentType,
      characterEncoding = request.getCharacterEncoding,
      protocol = request.getProtocol,
      serverName = request.getServerName,
      serverPort = request.getServerPort,
      remoteAddr = request.getRemoteAddr,
      remoteHost = request.getRemoteHost,
      remotePort = request.getRemotePort,
      localAddr = request.getLocalAddr,
      localName = request.getLocalName,
      localPort = request.getLocalPort,
      locale = request.getLocale,
      locales = request.getLocales.map(_.asInstanceOf[Locale]).toArray)

}

/**
 * A Action argument class to read header information.  Use it like
 * <pre>def myaction(c:Headers) = {
 *    val headerValue = c("name")
 * }
 * </pre>
 */
case class Headers(map: Map[String, String]) extends Args {

  def apply(k: String): String = map.get(k) match {
    case Some(a) => a
    case _ => null
  }

  def get(key: String): Option[String] = map.get(key)


  def getOrElse(key: String, alt: String): String = map.getOrElse(key, alt)
}

/**
 * Constructs a Headers class from the request.
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
 *	The Logged in name and the roles the user posseses.  Similar to HttpServletRequest.getUserPrincipal().
 * @param name the user name. 
 * @param roles the roles the user has 
 */
@serializable
case class Principal(name: String, roles: Roles)

object Principal {
  def get(request: HttpServletRequest) = {
    Option(
      if (request.getSession(false) != null)
        request.getSession.getAttribute("brzy_principal").asInstanceOf[Principal]
      else
        null
    )
  }
}

/**
 * The current session.  If a session does not exist, this will not create one, it will just return
 * an empty map.
 */
case class Session(map: Map[String, AnyRef]) extends Args {

  def get(key: String) = map.get(key)
}

/**
 * Constructs a Session arg from the servlet request.
 */
object Session {

  def apply(request: HttpServletRequest) = {
    val map = collection.mutable.Map[String, AnyRef]()

    if (request != null && request.getSession(false) != null)
      request.getSession.getAttributeNames.foreach(f => {
        val str = f.asInstanceOf[String]
        map += str -> request.getSession.getAttribute(str)
      })

    new Session(map.toMap)
  }
}

/**
 * The request parameters and the parameters embedded in the url.
 */
case class Parameters(map: Map[String, Array[String]]) extends Args {

  def apply(k: String) = map.get(k) match {
    case Some(a) => a(0)
    case _ => null
  }

  def get(key: String): Option[String] =
    if (map.contains(key))
      Option(map(key)(0))
    else
      None

  def getOrElse(key: String, alt: String) = {
    if (map.contains(key))
      map(key)(0)
    else
      alt
  }

  def array(key: String): Option[Array[String]] = map.get(key)
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
 *  Constructs the cookies from the servlet request.
 */
object Cookies {
  def apply(request: HttpServletRequest): Cookies = {
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
 * An individual cookie from the servlet request.
 */
case class Cookie(comment: String, domain: String, maxAge: Int, name: String,
        path: String, secure: Boolean, value: String, version: Int)

/**
 * This enables access to the the body of the post as a plain
 * text object to be used in parsing text, xml or json post data.
 *
 * @see http ://commons.apache.org/fileupload/using.html
 * @author Michael Fortin
 */
case class PostBody(request: HttpServletRequest) extends Args {
  private[this] val log = LoggerFactory.getLogger(classOf[PostBody])
  protected[this] val maxSize = 10000000
  protected[this] val tempDir = new java.io.File(util.Properties.tmpDir)
  protected[this] val sizeThreshold = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD

  def asText = Source.fromInputStream(request.getInputStream).mkString

  def asXml = XML.load(request.getInputStream)

  def asJson = tJson.parse(asText)

	/**
	 * This uses the apache commons fileupload api to digest the multipart content of the body.  This
	 * is used when uploading a file.  
	 *
	 * @param name The name of the parameter the data is uploaded as.
	 */
  def uploadedFile(name: String): FileItem = {
    if (ServletFileUpload.isMultipartContent(request)) {
      val factory = new DiskFileItemFactory()
      factory.setSizeThreshold(sizeThreshold)
      factory.setRepository(tempDir)
      val upload = new ServletFileUpload(factory)
      upload.setSizeMax(maxSize)
      val items = upload.parseRequest(request)
      log.debug("items: {}",items.mkString("[",",","]"))

      val item = items.find(x => {
        val i = x.asInstanceOf[FileItem]
        !i.isFormField && i.getFieldName == name
      })

      item match {
        case Some(i) => i.asInstanceOf[FileItem]
        case _ => error("Not a byte array.")
      }
    }
    else {
      error("Not a multipart upload")
    }
  }
}

trait AttributesTrait {

}

case class Attributes(
    url:Map[String,String],
    request: Map[String,Array[String]],
    session: Map[String,AnyRef],
    application : Map[String,AnyRef]
) extends Args

object Attributes {
  def apply(req: HttpServletRequest, url:Map[String,String]) = {
    val session = if(req.getSession(false) != null)
        for (name <- req.getSession.getAttributeNames) yield {
          name.asInstanceOf[String] -> req.getSession.getAttribute(name.asInstanceOf[String])
        }
      else
        Map.empty[String,AnyRef]

    // todo this is using the wrong servlet api, and will create session if you don't want it too
    val application = for (name <- req.getSession.getServletContext.getAttributeNames) yield {
      name.asInstanceOf[String] -> req.getSession.getServletContext.getAttribute(name.asInstanceOf[String])
    }
    val request = req.getParameterMap.toMap.asInstanceOf[Map[String,Array[String]]]
    new Attributes(url, request, session.toMap, application.toMap)
  }
}