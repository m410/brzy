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
package org.brzy.action.response

import org.brzy.action.Parser
import net.liftweb.json._
import java.io.OutputStream
import org.brzy.action.args.{ParametersRequest, Parameters}
import javax.servlet.{AsyncContext, AsyncListener}
import org.brzy.fab.interceptor.ManagedThreadContext
import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.brzy.persistence.Transaction

/**
 * What direction to send the client request too.  Can be a vew, redirect, forward, etc.
 * 
 * @author Michael Fortin
 */
sealed trait Direction extends Response

case object NoView extends Direction

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
    <class></class>
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

  def parse = {
    implicit val formats = Serialization.formats(NoTypeHints)
    Serialization.write(target)
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
    implicit val formats = Serialization.formats(NoTypeHints)
    val sb = new StringBuilder()
    sb.append(callback)
    sb.append("(")
    sb.append(Serialization.write(target))
    sb.append(")")
    sb.toString()
  }
}

/**
 *
 * @param action
 * @param timeout
 * @param listener
 *
 * @author Michael Fortin
 */
case class Async(action: (Parameters) => AnyRef, timeout: Int = 0, listener: AsyncListener = new BlankAsyncListener) {


  /**
   *
   * @param threadLocalSessions
   * @param trans
   * @param asyncContext
   * @return
   */
  def start(threadLocalSessions: List[ManagedThreadContext], trans: Transaction, asyncContext: AsyncContext) = {
    asyncContext.addListener(listener)
    asyncContext.setTimeout(timeout)

    new Runnable {
      def run() {
        trans.doWith(threadLocalSessions, { () =>
          val request = asyncContext.getRequest.asInstanceOf[HttpServletRequest]
          val response = asyncContext.getRequest.asInstanceOf[HttpServletResponse]
          // todo fix url parameters
          val parameters = new ParametersRequest(request, Map.empty[String, String])
          val result = action(parameters)
          ResponseHandler.apply(null, result, request, response)
        })
      }
    }
  }
}
/**
 * Return an error to the client, eg. 403.
 */
case class Error(code: Int, msg: String) extends Direction