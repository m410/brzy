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


import org.slf4j.LoggerFactory

import org.brzy.webapp.controller._
import org.brzy.webapp.view.FlashMessage

import collection.JavaConversions.JMapWrapper
import collection.mutable.ListBuffer

import java.util.Enumeration
import java.io.ByteArrayInputStream
import javax.servlet.http.{HttpServletResponse => Response, HttpServletRequest => Request, Cookie=>JCookie}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
trait Action extends Ordered[Action] {
  def actionPath: String

  def argTypes: List[AnyRef]

  def returnType: AnyRef

  def execute(args: List[AnyRef],principal:Option[Principal]): AnyRef

  def controller: Controller

  def view: String

  def constraints: List[Constraint]

  val path = Path(controller.basePath, actionPath)

  def authorize(p: Principal) = {
    if (p == null) {
      false
    }
    else {
      val sec = controller.asInstanceOf[Secured]
      p.roles.allowed.find(role => sec.roles.allowed.contains(role)).isDefined ||
              constraints.find(c => {
                if (c.isInstanceOf[Roles]) {
                  val roles = c.asInstanceOf[Roles]
                  p.roles.allowed.find(role => roles.allowed.contains(role)).isDefined
                }
                else
                  false
              }).isDefined
    }
  }

  def isSecure = controller.isInstanceOf[Secured]

  /**
   * the default view to display if needed.
   */
  val defaultView = {
    if (view.startsWith("/")) {
      view
    }
    else {
      val clazz = controller.getClass
      val folder: String =
        if (clazz.getSimpleName.indexOf("Controller") == -1)
          clazz.getSimpleName
        else
          clazz.getSimpleName.substring(0, clazz.getSimpleName.indexOf("Controller"))

      new StringBuilder()
              .append("/")
              .append(folder.substring(0, 1).toLowerCase)
              .append(folder.substring(1))
              .append("/")
              .append(view)
              .toString
    }
  }

  protected[action] def toClassList(t: List[_]) = {
    t.map(t => {
      if (t.toString.endsWith(".type"))
        Class.forName(t.toString.substring(0, t.toString.length - 5))
      else
        Class.forName(t.toString)
    })
  }

  override def compare(that: Action): Int = path.compare(that.path)

  override def toString = new StringBuilder().
          append("Action('").append(actionPath).
          append("', ").append(controller.getClass.getSimpleName).
          append("[").append(actionPath).append("])").toString
}

/**
 *
 */
object Action {
  private[this] val log = LoggerFactory.getLogger(getClass)

  /**
   *
   */
  def apply[F <: AnyRef](path: String, view: String, action: F, constraints: Constraint*)
          (implicit m: Manifest[F], controller: Controller): Action = {

    if (action.isInstanceOf[Function0[_]]) {
      val t1 = m.typeArguments(0)
      new Action0[t1.type, Function0[t1.type]](
        path,
        view,
        action.asInstanceOf[Function0[t1.type]],
        controller,
        constraints.toList)
    }
    else if (action.isInstanceOf[Function1[_, _]]) {
      val t2 = m.typeArguments(1)
      val t1 = m.typeArguments(0)
      new Action1[t1.type, t2.type, Function1[t1.type, t2.type]](
        path,
        view,
        action.asInstanceOf[Function1[t1.type, t2.type]],
        controller,
        constraints.toList)
    }
    else if (action.isInstanceOf[Function2[_, _, _]]) {
      val t3 = m.typeArguments(2)
      val t2 = m.typeArguments(1)
      val t1 = m.typeArguments(0)
      new Action2[t1.type, t2.type, t3.type, Function2[t1.type, t2.type, t3.type]](
        path,
        view,
        action.asInstanceOf[Function2[t1.type, t2.type, t3.type]],
        controller,
        constraints.toList)
    }
    else if (action.isInstanceOf[Function3[_, _, _, _]]) {
      val t4 = m.typeArguments(3)
      val t3 = m.typeArguments(2)
      val t2 = m.typeArguments(1)
      val t1 = m.typeArguments(0)
      new Action3[t1.type, t2.type, t3.type, t4.type, Function3[t1.type, t2.type, t3.type, t4.type]](
        path,
        view,
        action.asInstanceOf[Function3[t1.type, t2.type, t3.type, t4.type]],
        controller,
        constraints.toList)
    }
    else if (action.isInstanceOf[Function4[_,_, _, _, _]]) {
      val t5 = m.typeArguments(4)
      val t4 = m.typeArguments(3)
      val t3 = m.typeArguments(2)
      val t2 = m.typeArguments(1)
      val t1 = m.typeArguments(0)
      new Action4[t1.type, t2.type, t3.type, t4.type, t5.type, Function4[t1.type, t2.type, t3.type, t4.type, t5.type]](
        path,
        view,
        action.asInstanceOf[Function4[t1.type, t2.type, t3.type, t4.type, t5.type]],
        controller,
        constraints.toList)
    }
    else
      error("To many arguments, only 4 arguments are allowed currently")
  }

  /**
   *
   */
  class Action0[R, F <: Function0[R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(0)

    def argTypes: List[AnyRef] = List.empty[AnyRef]

    def execute(args: List[AnyRef],principal:Option[Principal]) = {
      if (controller.isInstanceOf[Intercepted]) {
        val wrap = () => {
          action.apply().asInstanceOf[AnyRef]
        }
        controller.asInstanceOf[Intercepted].intercept(wrap, args, principal)
      }
      else {
        action.apply().asInstanceOf[AnyRef]
      }
    }
  }

  /**
   *
   */
  class Action1[A, R, F <: Function1[A, R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(1)

    def argTypes: List[AnyRef] = toClassList(m.typeArguments.slice(0, 1))

    def execute(args: List[AnyRef],principal:Option[Principal]) = {
      if (controller.isInstanceOf[Intercepted]) {
        val wrap = () => {
          action.apply(args(0).asInstanceOf[A]).asInstanceOf[AnyRef]
        }
        controller.asInstanceOf[Intercepted].intercept(wrap, args, principal)
      }
      else {
        action.apply(args(0).asInstanceOf[A]).asInstanceOf[AnyRef]
      }
    }
  }

  /**
   *
   */
  class Action2[A1, A2, R, F <: Function2[A1, A2, R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(2)

    def argTypes: List[AnyRef] = toClassList(m.typeArguments.slice(0, 2))

    def execute(args: List[AnyRef],principal:Option[Principal]) = {
      if (controller.isInstanceOf[Intercepted]) {
        val wrap = () => {
          action.apply(args(0).asInstanceOf[A1], args(1).asInstanceOf[A2]).asInstanceOf[AnyRef]
        }
        controller.asInstanceOf[Intercepted].intercept(wrap, args, principal)
      }
      else
        action.apply(args(0).asInstanceOf[A1], args(1).asInstanceOf[A2]).asInstanceOf[AnyRef]
    }
  }

  /**
   *
   */
  class Action3[A1, A2, A3, R, F <: Function3[A1, A2, A3, R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(3)

    def argTypes: List[AnyRef] = toClassList(m.typeArguments.slice(0, 3))

    def execute(args: List[AnyRef],principal:Option[Principal]) = {
      if (controller.isInstanceOf[Intercepted]) {
        val wrap = () => {
          action.apply(args(0).asInstanceOf[A1], args(1).asInstanceOf[A2], args(2).asInstanceOf[A3]).asInstanceOf[AnyRef]
        }
        controller.asInstanceOf[Intercepted].intercept(wrap, args, principal)
      }
      else
        action.apply(args(0).asInstanceOf[A1], args(1).asInstanceOf[A2], args(2).asInstanceOf[A3]).asInstanceOf[AnyRef]
    }
  }

  /**
   *
   */
  class Action4[A1, A2, A3, A4, R, F <: Function4[A1, A2, A3, A4, R]](
          val actionPath: String,
          val view: String,
          val action: F,
          val controller: Controller,
          val constraints: List[Constraint]) (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(4)

    def argTypes: List[AnyRef] = toClassList(m.typeArguments.slice(0, 4))

    def execute(args: List[AnyRef],principal:Option[Principal]) = {
      if (controller.isInstanceOf[Intercepted]) {
        val wrap = () => {
          action.apply(args(0).asInstanceOf[A1], args(1).asInstanceOf[A2], args(2).asInstanceOf[A3], args(3).asInstanceOf[A4]).asInstanceOf[AnyRef]
        }
        controller.asInstanceOf[Intercepted].intercept(wrap, args, principal)
      }
      else
        action.apply(args(0).asInstanceOf[A1], args(1).asInstanceOf[A2], args(2).asInstanceOf[A3], args(3).asInstanceOf[A4]).asInstanceOf[AnyRef]
    }
  }


  /**
   * TODO need to hand the three return types, data, direction and stream
   */
  def handleResults(action: Action, actionResult: AnyRef, req: Request, res: Response): Unit = {
    log.debug("results: {}", actionResult)

    def matchData(result: Any): Unit = result match {
      case (s: String, m: AnyRef) =>
        log.debug("tuple: ({},{})", s, m)
        handleData(Model(s -> m), req, res)
      case d: Data =>
        log.debug("Data: {}", d)
        handleData(d, req, res)
      case tup: (_, _) =>
        log.debug("tuple: {}", tup)
        tup.productIterator.foreach(s => matchData(s))
      case r: (_, _, _) =>
        log.debug("tuple: {}", r)
        r.productIterator.foreach(s => matchData(s))
      case r: (_, _, _, _) =>
        log.debug("tuple: {}", r)
        r.productIterator.foreach(s => matchData(s))
      case r: (_, _, _, _, _) =>
        log.debug("tuple: {}", r)
        r.productIterator.foreach(s => matchData(s))
      case _ => //ignore and Direction in the list
    }

    matchData(actionResult)

    // need to handle the direction after the data or a servlet error doesn't happen
    def matchDirection(directionResult: Any): Unit = directionResult match {
      case d: Direction =>
        log.debug("Direction: {}", d)
        handleDirection(action, d, req, res)
      case d: Data => // ignore it
      case (s: String, m: AnyRef) =>
        log.debug("tuple default: {}", action.defaultView)
        handleDirection(action, View(action.defaultView), req, res)
      case tup: (_, _) =>
        tup.productIterator.foreach(s => matchDirection(s))
      case r: (_, _, _) =>
        r.productIterator.foreach(s => matchDirection(s))
      case r: (_, _, _, _) =>
        r.productIterator.foreach(s => matchDirection(s))
      case r: (_, _, _, _, _) =>
        r.productIterator.foreach(s => matchDirection(s))
      case _ =>
        log.debug("default: {}", action.defaultView)
        handleDirection(action, View(action.defaultView), req, res)
    }

    actionResult match {
      case d: Data =>
        handleDirection(action, View(action.defaultView), req, res)
      case _ =>
        matchDirection(actionResult)
    }
  }

  /**
   * there can only be one direction
   */
  protected[action] def handleDirection(action: Action, direct: Direction, req: Request, res: Response) =
    direct match {
      case view: View =>
        val target: String = view.path + ".ssp" //action.viewType
        log.debug("view: {}", target)
				res.setHeader("Content-Type","text/html; charset=utf-8") // TODO Should be set by the view and overridable by the controller
        req.getRequestDispatcher(target).forward(req, res)
      case f: Forward =>
        log.debug("forward: {}", f)
        req.getRequestDispatcher(f.path + ".brzy").forward(req, res)
      case s: Redirect =>
        log.debug("redirect: {}", s)
        val target: String =
          if (s.path.startsWith("http"))
            s.path
          else if(req.getContextPath.endsWith("/") && s.path.startsWith("/")  )
						req.getContextPath + s.path.substring(1,s.path.length)
					else
						req.getContextPath + s.path
        res.sendRedirect(target)
      case s: Error =>
        log.debug("Error: {}", s)
        res.sendError(s.code, s.msg)
      case x: Xml =>
        log.debug("xml: {}", x)
        res.setContentType(x.contentType)
        res.getWriter.write(x.parse)
      case t: Text =>
        log.debug("text: {}", t)
        res.setContentType(t.contentType)
        res.getWriter.write(t.parse)
      case b: Binary =>
        log.debug("bytes: {}", b)
        res.setContentType(b.contentType)
				res.setHeader("content-length",b.bytes.length.toString)
				val input = new ByteArrayInputStream(b.bytes)
				var inRead = 0
				while({inRead = input.read;inRead} >= 0)
        	res.getOutputStream.write(inRead)
      case j: Json =>
        log.debug("json: {}", j)
        res.setContentType(j.contentType)
        res.getWriter.write(j.parse)
      case j: Jsonp =>
        log.debug("jsonp: {}", j)
        res.setContentType(j.contentType)
        res.getWriter.write(j.parse)
      case _ => error("Unknown Driection: %s".format(direct))
    }

  /**
   * There can be several data types
   */
  protected[action] def handleData(data: Data, req: Request, res: Response) =
    data match {
      case model: Model =>
        log.debug("model: {}", model)
        model.attrs.foreach(s => req.setAttribute(s._1, s._2))
      case s: SessionAdd =>
        log.debug("sessionAdd: {}", s)
        s.attrs.foreach(nvp => req.getSession.setAttribute(nvp._1, nvp._2))
      case s: Flash =>
        log.debug("flash: {}", s)
        new FlashMessage(s.code, req.getSession)
      case s: CookieAdd =>
        log.debug("cookieAdd: {}", s)
        res.addCookie(new JCookie(s.attrs._1, s.attrs._2.toString))
      case h: ResponseHeaders =>
        log.debug("response headers: {}", h)
        h.headers.foreach(r=>{res.setHeader(r._1,r._2)})
      case s: SessionRemove =>
        log.debug("sessionRemove: {}", s)
        req.getSession.removeAttribute(s.attr)
      case _ => error("Unknown Data: %s".format(data))
    }

  /**
   *
   */
  def buildArgs(action: Action, req: Request) = {
    val path = parseActionPath(req.getRequestURI, req.getContextPath)
    val args = action.argTypes
    val list = new ListBuffer[AnyRef]()
    log.debug("action:", args)
    log.debug("arg types: {}, path: {}", args, path)

    args.toList.foreach(arg => arg match {
      case ParametersClass =>
        val urlParams = action.path.extractParameterValues(path) //.matchParameters(path)
        val paramMap = new collection.mutable.HashMap[String, Array[String]]()

        for (i <- 0 to urlParams.size - 1)
          paramMap.put(action.path.parameterNames(i), Array(urlParams(i)))
        
        val jParams = req.getParameterMap.asInstanceOf[java.util.Map[String, Array[String]]]
        val wrappedMap = new JMapWrapper[String, Array[String]](jParams)
        list += new Parameters(wrappedMap.toMap ++ paramMap.toMap)
      case SessionClass =>
        list += Session(req)
      case RequestAttributesClass =>
        list += RequestAttributes(req)
      case HeadersClass =>
        val headers = Headers(req)
        list += headers
      case CookiesClass =>
        list += Cookies(req)
      case PostBodyClass =>
        list += PostBody(req)
      case PrincipalClass =>
        list += req.getSession.getAttribute("brzy_principal").asInstanceOf[Principal]
      case _ =>
        error("unknown action argument type: " + arg)
    })
    log.debug("args: {}", list)
    list.toList
  }

  def parseActionPath(uri: String, ctx: String) = {
    val newuri =
      if (uri.startsWith("//"))
        uri.substring(1, uri.length)
      else
        uri

    if (newuri.endsWith(".brzy") && (ctx == "" || ctx == "/"))
      newuri.substring(0, newuri.length - 5)
    else if (newuri.endsWith(".brzy") && (ctx != "" || ctx != "/"))
      newuri.substring(ctx.length, newuri.length - 5)
    else if (!newuri.endsWith(".brzy") && (ctx != "" || ctx != "/"))
      newuri.substring(ctx.length, newuri.length)
    else
      newuri
  }
}