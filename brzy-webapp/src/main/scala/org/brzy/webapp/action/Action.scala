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

import java.io.ByteArrayInputStream
import javax.servlet.http.{HttpServletResponse => Response, HttpServletRequest => Request, Cookie => JCookie}
import javax.management.remote.rmi._RMIConnection_Stub

/**
 * An action is an entry point into the application.  A controller will have one or more actions.
 * Actions
 * <code>
 * class MyController extends Controller("path"){
 *   def actions = List(Action("","",view _))
 *   def view = "name"->"value
 * }
 * </code>
 *
 * @author Michael Fortin
 */
trait Action extends Ordered[Action] {
  def actionPath: String

  /**
   * A list of arguments types needed to execute the action.  The argument types must be a subclass
   * of Args.
   */
  def argTypes: List[AnyRef]

  /**
   * The return type of the action.  The return type must be a subclass of either Data or
   * Direction.
   */
  def returnType: AnyRef

  def execute(args: List[AnyRef], principal: Option[Principal]): AnyRef

  /**
   * The reference to the parent controller
   */
  def controller: Controller

  /**
   * The default view to render if not explicitly set in the return types.
   */
  def view: String

  /**
   * A list of constraints to be applied to the action.  A constraint will allow or disallow the
   * execution of the action.
   */
  def constraints: List[Constraint]

  /**
   * The RESTful like path called by a client to execute this action.
   */
  val path = Path(controller.basePath, actionPath)

  /**
   * For secure actions, this is called to test the users permission to execute it.
   */
  def isAuthorized(p: Option[Principal]) = p match {
    case Some(principal) =>
      if (constraints.find(_.isInstanceOf[Roles]).isDefined)
        secureConstraints(constraints, principal)
      else
        secureConstraints(controller.constraints, principal)
    case _ => false
  }


  def isConstrained(r:Request) = {
    val y = nonSecureConstraints(constraints,r)
    val x = nonSecureConstraints(controller.constraints,r)
    y || x
  }

  protected[this] def nonSecureConstraints(constraints:List[Constraint], request:Request) = {
    println("constraints="+constraints)
    !constraints.forall(constraint => constraint match {
      case Secure(allowed) =>
        request.isSecure == allowed
      case c:ContentTypes =>
        c.allowed.contains(request.getContentType)
      case h:HttpMethods =>
        val methodName = HttpMethod.withName(request.getMethod.toUpperCase)
        h.allowed.find(_ == methodName).isDefined
      case r:Roles =>
        true
      case _ =>
        false
    })
  }

  protected[this] def secureConstraints(constraints:List[Constraint], p:Principal) = {
    println("constraints="+constraints)
    constraints.forall(constraint => constraint match {
      case r:Roles =>
        r.allowed.find(x=>p.roles.allowed.contains(x)).isDefined
      case _ =>
        true
    })
  }


  /**
   * Determines if this action requires authentication or not.
   */
  def isSecured = controller.isInstanceOf[Secured]

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
              .toString()
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

  override def toString = {
    val buffer = new StringBuilder().
          append("Action[").append(controller.basePath).
          append(", ").append(actionPath)

    if (!constraints.isEmpty)
      buffer.append(", ").append(constraints)
    
    buffer.append("]")
    buffer.toString()
  }
}

/**
 * Factory methods for constructing Actions
 */
object Action {
  private[this] val log = LoggerFactory.getLogger(getClass)

  /**
   * Construct an action depending on how many arguments there are.
   */
  def apply[F <: AnyRef](path: String, view: String, action: F, constraints: Constraint*)
          (implicit m: Manifest[F], controller: Controller): Action = {

    if (action.isInstanceOf[() => _]) {
      val t1 = m.typeArguments(0)
      new Action0[t1.type, () => t1.type](
        path,
        view,
        action.asInstanceOf[() => t1.type],
        controller,
        constraints.toList)
    }
    else if (action.isInstanceOf[(_) => _]) {
      val t2 = m.typeArguments(1)
      val t1 = m.typeArguments(0)
      new Action1[t1.type, t2.type, (t1.type) => t2.type](
        path,
        view,
        action.asInstanceOf[(t1.type) => t2.type],
        controller,
        constraints.toList)
    }
    else if (action.isInstanceOf[(_, _) => _]) {
      val t3 = m.typeArguments(2)
      val t2 = m.typeArguments(1)
      val t1 = m.typeArguments(0)
      new Action2[t1.type, t2.type, t3.type, (t1.type, t2.type) => t3.type](
        path,
        view,
        action.asInstanceOf[(t1.type, t2.type) => t3.type],
        controller,
        constraints.toList)
    }
    else if (action.isInstanceOf[(_, _, _) => _]) {
      val t4 = m.typeArguments(3)
      val t3 = m.typeArguments(2)
      val t2 = m.typeArguments(1)
      val t1 = m.typeArguments(0)
      new Action3[t1.type, t2.type, t3.type, t4.type, (t1.type, t2.type, t3.type) => t4.type](
        path,
        view,
        action.asInstanceOf[(t1.type, t2.type, t3.type) => t4.type],
        controller,
        constraints.toList)
    }
    else if (action.isInstanceOf[(_, _, _, _) => _]) {
      val t5 = m.typeArguments(4)
      val t4 = m.typeArguments(3)
      val t3 = m.typeArguments(2)
      val t2 = m.typeArguments(1)
      val t1 = m.typeArguments(0)
      new Action4[t1.type, t2.type, t3.type, t4.type, t5.type, (t1.type, t2.type, t3.type, t4.type) => t5.type](
        path,
        view,
        action.asInstanceOf[(t1.type, t2.type, t3.type, t4.type) => t5.type],
        controller,
        constraints.toList)
    }
    else
      error("To many arguments, only 4 arguments are allowed currently")
  }

  /**
   * An Action that takes no arguments
   */
  class Action0[R, F <: Function0[R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(0)

    def argTypes: List[AnyRef] = List.empty[AnyRef]

    def execute(args: List[AnyRef], principal: Option[Principal]) = {
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
   * An action with a single argument that implements Args.
   */
  class Action1[A, R, F <: Function1[A, R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(1)

    def argTypes: List[AnyRef] = toClassList(m.typeArguments.slice(0, 1))

    def execute(args: List[AnyRef], principal: Option[Principal]) = {
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
   * An action with two arguments.  All arguments must be a subclass of Args.
   */
  class Action2[A1, A2, R, F <: Function2[A1, A2, R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(2)

    def argTypes: List[AnyRef] = toClassList(m.typeArguments.slice(0, 2))

    def execute(args: List[AnyRef], principal: Option[Principal]) = {
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
   * An action with three arguments.  All arguments must be a subclass of Args.
   */
  class Action3[A1, A2, A3, R, F <: Function3[A1, A2, A3, R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(3)

    def argTypes: List[AnyRef] = toClassList(m.typeArguments.slice(0, 3))

    def execute(args: List[AnyRef], principal: Option[Principal]) = {
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
   * An action with four arguments.  All arguments must be a subclass of Args.
   */

  class Action4[A1, A2, A3, A4, R, F <: Function4[A1, A2, A3, A4, R]](
          val actionPath: String,
          val view: String,
          val action: F,
          val controller: Controller,
          val constraints: List[Constraint])(implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(4)

    def argTypes: List[AnyRef] = toClassList(m.typeArguments.slice(0, 4))

    def execute(args: List[AnyRef], principal: Option[Principal]) = {
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
   * Handles the return type of the action.  This performs duties like setting session variables
   * in the HttpSession.
   */
  def handleResults(action: Action, actionResult: AnyRef, req: Request, res: Response) {
    log.debug("results: {}", actionResult)

    def matchData(result: Any) {
      result match {
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
    }

    matchData(actionResult)

    // need to handle the direction after the data or a servlet error doesn't happen
    def matchDirection(directionResult: Any) {
      directionResult match {
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
    }

    actionResult match {
      case d: Data =>
        handleDirection(action, View(action.defaultView), req, res)
      case _ =>
        matchDirection(actionResult)
    }
  }

  /**
   * An Action can only return one instance of a Direction.  This will execute it.  For example
   * a Redirect will be run as an 'HttpServletResponse.sendRedirect(target)'.
   */
  protected[action] def handleDirection(action: Action, direct: Direction, req: Request, res: Response) =
    direct match {
      case view: View =>
        val target: String = view.path + ".ssp" //action.viewType
        log.debug("view: {}", target)
        // TODO Should be set by the view and overridable by the controller
	res.setHeader("Content-Type","text/html; charset=utf-8")
        req.getRequestDispatcher(target).forward(req, res)
      case f: Forward =>
        log.debug("forward: {}", f)
        req.getRequestDispatcher(f.path + ".brzy").forward(req, res)
      case s: Redirect =>
        log.debug("redirect: {}", s)
        val target: String =
          if (s.path.startsWith("http"))
            s.path
          else if (req.getContextPath.endsWith("/") && s.path.startsWith("/"))
            req.getContextPath + s.path.substring(1, s.path.length)
          else
            req.getContextPath + s.path
        res.sendRedirect(target)
      case s: Error =>
        log.debug("Error: {}", s)
        res.sendError(s.code, s.msg)
      case x: Xml[_] =>
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
        res.setHeader("content-length", b.bytes.length.toString)
        val input = new ByteArrayInputStream(b.bytes)
        var inRead = 0
        while ( { inRead = input.read; inRead} >= 0)
          res.getOutputStream.write(inRead)
      case j: Json[_] =>
        log.debug("json: {}", j)
        res.setContentType(j.contentType)
        res.getWriter.write(j.parse)
      case j: Jsonp[_] =>
        log.debug("jsonp: {}", j)
        res.setContentType(j.contentType)
        res.getWriter.write(j.parse)
      case _ => error("Unknown Driection: %s".format(direct))
    }

  /**
   * While an action can only return zero or one Direction, it can return zero or more Data
   * subclasses.  This Handles their execution.
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
      case h: ResponseHeaders =>
        log.debug("response headers: {}", h)
        h.headers.foreach(r => {
          res.setHeader(r._1, r._2)
        })
      case s: SessionRemove =>
        log.debug("sessionRemove: {}", s)
        req.getSession.removeAttribute(s.attr)
      case _ => error("Unknown Data: %s".format(data))
    }

  /**
   * This converts the http servlet request parameters and attributes into the action Arg types
   * required for each action to execute.
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

  /**
   * Converts the RESTful uri to and internal representation.
   */
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
