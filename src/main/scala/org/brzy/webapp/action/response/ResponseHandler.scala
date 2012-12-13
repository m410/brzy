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

import java.io.ByteArrayInputStream
import org.brzy.webapp.view.FlashMessage
import javax.servlet.http.{Cookie=>JCookie, HttpServletResponse, HttpServletRequest}
import org.slf4j.LoggerFactory
import org.brzy.webapp.action.Action

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
object ResponseHandler {

  val log = LoggerFactory.getLogger(getClass)

  /**
   * Handles the return type of the action.  This performs duties like setting session variables
   * in the HttpSession.
   */
  def apply(action: Action, actionResult: AnyRef, req: HttpServletRequest, res: HttpServletResponse) {
    log.trace("results: {}", actionResult)
    matchData(actionResult,req,res)
    // need to handle the direction after the data or a servlet error doesn't happen

    actionResult match {
      case d: Data =>
        handleDirection(action, View(action.defaultView), req, res)
      case _ =>
        matchDirection(actionResult,action,req,res)
    }
  }

  private def matchData(result: Any, req:HttpServletRequest,res:HttpServletResponse) {
    result match {
      case (s: String, m: AnyRef) =>
        log.trace("tuple: ({},{})", Array(s, m):_*)
        handleData(Model(s -> m), req, res)
      case d: Data =>
        log.trace("Data: {}", d)
        handleData(d, req, res)
      case tup: (_, _) =>
        log.trace("tuple: {}", tup)
        tup.productIterator.foreach(s => matchData(s,req,res))
      case r: (_, _, _) =>
        log.trace("tuple: {}", r)
        r.productIterator.foreach(s => matchData(s,req,res))
      case r: (_, _, _, _) =>
        log.trace("tuple: {}", r)
        r.productIterator.foreach(s => matchData(s,req,res))
      case r: (_, _, _, _, _) =>
        log.trace("tuple: {}", r)
        r.productIterator.foreach(s => matchData(s,req,res))
      case _ => //ignore and Direction in the list
    }
  }

  private  def matchDirection(directionResult: Any, action:Action, req:HttpServletRequest,res:HttpServletResponse) {
    directionResult match {
      case d: Direction =>
        log.trace("Direction: {}", d)
        handleDirection(action, d, req, res)
      case d: Data => // ignore it
      case (s: String, m: AnyRef) =>
        log.trace("tuple default: {}", action.defaultView)
        handleDirection(action, View(action.defaultView), req, res)
      case tup: (_, _) =>
        tup.productIterator.foreach(s => matchDirection(s,action,req,res))
      case r: (_, _, _) =>
        r.productIterator.foreach(s => matchDirection(s,action,req,res))
      case r: (_, _, _, _) =>
        r.productIterator.foreach(s => matchDirection(s,action,req,res))
      case r: (_, _, _, _, _) =>
        r.productIterator.foreach(s => matchDirection(s,action,req,res))
      case _ =>
        log.trace("default: {}", action.defaultView)
        handleDirection(action, View(action.defaultView), req, res)
    }
  }


  /**
   * An Action can only return one instance of a Direction.  This will execute it.  For example
   * a Redirect will be run as an 'HttpServletResponse.sendRedirect(target)'.
   */
  private def handleDirection(action: Action, direct: Direction, req: HttpServletRequest, res: HttpServletResponse)  {
    direct match {
      case view: View =>
        val target: String = view.path + ".ssp" //action.viewType
        log.trace("view: {}", target)
        // TODO Should be set by the view and override-able by the controller
        res.setHeader("Content-Type","text/html; charset=utf-8")
        req.getRequestDispatcher(target).forward(req, res)
      case f: Forward =>
        log.trace("forward: {}", f)
        req.getRequestDispatcher(f.path + ".brzy").forward(req, res)
      case s: Redirect =>
        log.trace("redirect: {}", s)
        val target: String =
          if (s.path.startsWith("http"))
            s.path
          else if (req.getContextPath.endsWith("/") && s.path.startsWith("/"))
            checkSecured(req, req.getContextPath + s.path.substring(1, s.path.length))
          else
            checkSecured(req, req.getContextPath + s.path)
        res.sendRedirect(target)
      case s: Error =>
        log.trace("Error: {}", s)
        res.sendError(s.code, s.msg)
      case x: Xml[_] =>
        log.trace("xml: {}", x)
        res.setContentType(x.contentType)
        res.getWriter.write(x.parse)
      case t: Text =>
        log.trace("text: {}", t)
        res.setContentType(t.contentType)
        res.getWriter.write(t.parse)
      case b: Binary =>
        log.trace("bytes: {}", b)
        res.setContentType(b.contentType)
        res.setHeader("content-length", b.bytes.length.toString)
        val input = new ByteArrayInputStream(b.bytes)
        var inRead = 0
        while ( { inRead = input.read; inRead} >= 0)
          res.getOutputStream.write(inRead)
      case s: Stream =>
        log.trace("stream: {}", s)
        res.setContentType(s.contentType)
        s.io(res.getOutputStream)
      case j: Json[_] =>
        log.trace("json: {}", j)
        res.setContentType(j.contentType)
        res.getWriter.write(j.parse)
      case j: Jsonp[_] =>
        log.trace("jsonp: {}", j)
        res.setContentType(j.contentType)
        res.getWriter.write(j.parse)
      case _ => throw new UnknownActionDirectionException("Unknown Driection: %s".format(direct))
    }
  }

  /**
   * Fixes an issue where the ssl is handled by a firewall or load-balancer, the request will
   * be in http, not https.  This explicitly sets the https protocol in the redirect url.
   * @param req http servlet request
   * @param basePath the absolute path of the request including servlet context
   * @return the context path for insecure redirect or the full path with ssl protocol.
   */
  private def checkSecured(req:HttpServletRequest, basePath:String) = {
    if(req.isSecure)
      "https://" + req.getServerName + basePath
    else
      basePath
  }

  /**
   * While an action can only return zero or one Direction, it can return zero or more Data
   * subclasses.  This Handles their execution.
   */
  private def handleData(data: Data, req: HttpServletRequest, res: HttpServletResponse) {
    data match {
      case model: Model =>
        log.trace("model: {}", model)
        model.attrs.foreach(s => req.setAttribute(s._1, s._2))
      case s: Session =>
        log.trace("session: {}", s)
        s.add.foreach(nvp => req.getSession.setAttribute(nvp._1, nvp._2))
        s.remove.foreach(a => req.getSession.removeAttribute(a))
        if (s.invalidate) {
          log.trace("session invalidated")
          req.getSession.invalidate()
        }
      case s: Flash =>
        log.trace("flash: {}", s)
        new FlashMessage(s.message, req.getSession)
      case s: Cookie =>
        log.trace("cookie: {}", s)
        val cookie = new JCookie(s.name, s.value)
        cookie.setPath(s.path match {
          case Some(p) => p
          case _ => req.getContextPath
        })
        cookie.setMaxAge(s.maxAge)
        cookie.setDomain(s.domain match {
          case Some(domain) => domain
          case _ => req.getServerName
        })
        res.addCookie(cookie)
      case h: Headers =>
        log.trace("response headers: {}", h)
        h.headers.foreach(r => {
          res.setHeader(r._1, r._2)
        })
      case _ =>
        throw new UnknownActionDataException("Unknown Data: %s".format(data))
    }
  }
}