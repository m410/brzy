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


import args.{Arg, Principal}
import org.slf4j.LoggerFactory

import org.brzy.webapp.controller._

import javax.servlet.http.{HttpServletRequest => Request}

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

  def execute(args: Array[Arg], principal: Principal): AnyRef

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
  def isAuthorized(principal: Principal) = {
    if (constraints.find(_.isInstanceOf[Roles]).isDefined)
      secureConstraints(constraints, principal)
    else
      secureConstraints(controller.constraints, principal)
  }


  def isConstrained(r:Request) = {
    val y = nonSecureConstraints(constraints,r)
    val x = nonSecureConstraints(controller.constraints,r)
    y || x
  }

  protected def nonSecureConstraints(constraints:List[Constraint], request:Request) = {
    !constraints.forall({
      case h:HttpMethods =>
        val methodName = HttpMethod.withName(request.getMethod.toUpperCase)
        h.allowed.find(_ == methodName).isDefined
      case c:ContentTypes =>
        c.allowed.find(_.toLowerCase == request.getContentType.toLowerCase).isDefined
      case Secure(allowed) =>
        true // ignored
      case r:Roles =>
        true // ignored
      case _ =>
        false // never happen, here to prevent compiler warning
    })
  }

  protected def secureConstraints(constraints:List[Constraint], p:Principal) = {
    constraints.forall(constraint => constraint match {
      case r:Roles =>
        if(p.isLoggedIn)
          r.allowed.find(x=>p.roles.allowed.contains(x)).isDefined
        else
          false
      case _ =>
        true
    })
  }

  def requiresSsl = {
    constraints.find(_.isInstanceOf[Secure]).isDefined || controller.constraints.find(_.isInstanceOf[Secure]).isDefined
  }

  /**
   * Determines if this action requires authentication or not.
   */
  def isSecured = controller.isInstanceOf[Authorization]

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

  protected def toClassList(t: List[_]) = {
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
      throw new RuntimeException("To many arguments, only 4 arguments are allowed currently")
  }

  /**
   * An Action that takes no arguments
   */
  class Action0[R, F <: Function0[R]](val actionPath: String, val view: String, val action: F,
          val controller: Controller, val constraints: List[Constraint])
          (implicit m: Manifest[F]) extends Action {
    def returnType: AnyRef = m.typeArguments(0)

    def argTypes: List[AnyRef] = List.empty[AnyRef]

    def execute(args: Array[Arg], principal: Principal) = {
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

    def execute(args: Array[Arg], principal: Principal) = {
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

    def execute(args: Array[Arg], principal: Principal) = {
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

    def execute(args: Array[Arg], principal: Principal) = {
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

    def execute(args: Array[Arg], principal: Principal) = {
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
}
