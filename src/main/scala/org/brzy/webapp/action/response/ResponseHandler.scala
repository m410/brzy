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


import org.brzy.webapp.view.FlashMessage
import org.brzy.webapp.action.Action
import org.brzy.webapp.application.WebApp

import javax.servlet.http.{Cookie=>JCookie, HttpServletResponse, HttpServletRequest}
import java.io.ByteArrayInputStream
import org.slf4j.LoggerFactory
import java.util.concurrent.{ThreadPoolExecutor, TimeUnit, LinkedBlockingQueue}
import com.sun.tools.internal.jxc.SchemaGenerator.Runner
import javax.servlet.{AsyncEvent, AsyncListener}

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
    log.debug("results: {}", actionResult)
    pullData(actionResult).foreach(handleData(_,req,res))
    val direction = pullDirection(actionResult, action.view)
    handleDirection(action,direction,req,res)
  }

  private def pullDirection(actionResult: AnyRef, actionView:Direction):Direction = {
    val directionOption = actionResult match {
      case Unit =>
        Option(actionView)
      case d: Direction =>
        Option(d)
      case d: Data => // ignore it
        Option(actionView)
      case (s: String, m: AnyRef) =>
        Option(actionView)
      case tup: (_, _) =>
        Option(tup.productIterator.find({
          case d:Direction => true
          case _ => false
        }).getOrElse(actionView).asInstanceOf[Direction])
      case r: (_, _, _) =>
        Option(r.productIterator.find({
          case d:Direction => true
          case _ => false
        }).getOrElse(actionView).asInstanceOf[Direction])
      case r: (_, _, _, _) =>
        Option(r.productIterator.find({
          case d:Direction => true
          case _ => false
        }).getOrElse(actionView).asInstanceOf[Direction])
      case r: (_, _, _, _, _) =>
        Option(r.productIterator.find({
          case d:Direction => true
          case _ => false
        }).getOrElse(actionView).asInstanceOf[Direction])
      case _ =>
        throw new ToManyActionReturnsException("Returns are limited to tuple of 5 values")
    }

    directionOption match {
      case Some(NoView) => throw new NoViewException("No Direction returned for action")
      case Some(definedDirection) => definedDirection
      case _ => throw new NoViewException("No Direction returned for action")
    }
  }

  private def pullData(actionResult: Any, data:Seq[Data] = Seq.empty[Data]):Seq[Data] = {
    actionResult match {
      case Unit =>
        Seq.empty[Data]
      case d: Direction =>
        Seq.empty[Data]
      case d: Data =>
        data ++ Seq(d)
      case (s: String, m: AnyRef) =>
        data ++ Seq(Model(s->m))
      case tup: (_, _) =>
        tup.productIterator.foldLeft(data)((d,t)=> d ++ pullData(t) )
      case r: (_, _, _) =>
        r.productIterator.foldLeft(data)((d,t)=> d ++ pullData(t) )
      case r: (_, _, _, _) =>
        r.productIterator.foldLeft(data)((d,t)=> d ++ pullData(t) )
      case r: (_, _, _, _, _) =>
        r.productIterator.foldLeft(data)((d,t)=> d ++ pullData(t) )
      case _ =>
        throw new ToManyActionReturnsException("Unknown response (tuples max is 5):"+ actionResult)
    }
  }



  /**
   * An Action can only return one instance of a Direction.  This will execute it.  For example
   * a Redirect will be run as an 'HttpServletResponse.sendRedirect(target)'.
   */
  private def handleDirection(action: Action, direct: Direction, req: HttpServletRequest, res: HttpServletResponse)  {
    direct match {
      case view: View =>
        val target: String = view.path + ".ssp" //todo fix hard-coding, need reference to application
        log.trace("view: {}", target)
        res.setHeader("Content-Type",view.contentType)
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
      case j: Async =>
        log.trace("async: {}", j)
        val asyncContext = req.startAsync(req,res)
        asyncContext.addListener(j.listener)
        val webapp = req.getServletContext.getAttribute("application").asInstanceOf[WebApp]
        asyncContext.start(j.run(action, webapp.threadLocalSessions, action.trans, asyncContext))
      //        log.trace("async: {}", j)
//        val asyncContext = req.startAsync()
//        asyncContext.addListener(new AsyncListener {
//          def onComplete(p1: AsyncEvent) {println(s"############# complete $p1")}
//          def onTimeout(p1: AsyncEvent) {println(s"############# timeout $p1")}
//          def onError(p1: AsyncEvent) {println(s"############# error $p1")}
//          def onStartAsync(p1: AsyncEvent) {println(s"############# on start $p1")}
//        })
//        val webapp = req.getServletContext.getAttribute("application").asInstanceOf[WebApp]
////        val executor = new ThreadPoolExecutor(10, 10, 50000L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue[Runnable](100))
////        executor.execute(j.start(action, webapp.threadLocalSessions, action.trans, asyncContext))
//        asyncContext.setTimeout(10000)
//        asyncContext.getResponse.getWriter.write(s"Hello there --")
//        asyncContext.getResponse.getWriter.flush()
//
//        asyncContext.start(new Runnable {
//          def run() {
//            var count = 1
//            while(true){
//              Thread.sleep(5000)
//              asyncContext.getResponse.getWriter.write(s"Hello there $count")
//              asyncContext.getResponse.getWriter.flush()
//              count = count + 1
//              println(s"############# count:$count")
//              if (count>5)
//                asyncContext.complete()
//            }
//          }
//        })
      case _ =>
        throw new UnknownActionDirectionException("Unknown Driection: %s".format(direct))
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