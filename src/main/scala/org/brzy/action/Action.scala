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
package org.brzy.action


import args.{Arg, Principal}

import javax.servlet.http.{HttpServletResponse, HttpServletRequest}
import org.brzy.persistence.Transaction
import org.brzy.action.response.{View, Direction}

import HttpMethod._
import org.brzy.controller.{Intercepted, Controller}

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

  def path: String

  def trans: Transaction

  def constrs: Seq[Constraint]

  def view: Direction

  def controller: Controller

  def async:Boolean

  def argTypes:Array[Class[_]]

  def isAuthorized(p:Principal):Boolean = {
    // todo implement me
    false
  }

  def paramsFor(uri:String):Map[String,String] = {
    // todo implement me
    Map.empty[String,String]
  }

  def isMatch(method: String, contentType: String, path: String) = {
    // todo implement me
    false
  }

  def doService(request: HttpServletRequest, response: HttpServletResponse) {
    // todo implement me
  }

  def compare(that: Action) = path.compareTo(that.path)

  override def toString = {
    val buffer = new StringBuilder().
            append("Action[").append(controller.basePath).
            append(", ").append(path)

    if (!constrs.isEmpty)
      buffer.append(", ").append(constrs)

    buffer.append("]")
    buffer.toString()
  }}

/**
 * Factory methods for constructing Actions
 */
object Action {

  /**
   * Construct an action depending on how many arguments there are.
   */
  def apply[F <: AnyRef:Manifest](
          path: String,
          action: F,
          transaction: Transaction,
          methods:Seq[HttpMethod],
          view:Direction,
          constraints: Seq[Constraint],
          async:Boolean,
          controller:Controller): Action = {

    if (action.isInstanceOf[() => _]) {
      val t1 = manifest[F].typeArguments(0)
      new Action0[t1.type, () => t1.type](
        path,
        view,
        action.asInstanceOf[() => t1.type],
        transaction,
        methods,
        constraints,
        async,
        controller)
    }
    else if (action.isInstanceOf[(_) => _]) {
      val t2 = manifest[F].typeArguments(1)
      val t1 = manifest[F].typeArguments(0)
      new Action1[t1.type, t2.type, (t1.type) => t2.type](
        path,
        view,
        action.asInstanceOf[(t1.type) => t2.type],
        transaction,
        methods,
        constraints,
        async,
        controller)
    }
    else if (action.isInstanceOf[(_, _) => _]) {
      val t3 = manifest[F].typeArguments(2)
      val t2 = manifest[F].typeArguments(1)
      val t1 = manifest[F].typeArguments(0)
      new Action2[t1.type, t2.type, t3.type, (t1.type, t2.type) => t3.type](
        path,
        view,
        action.asInstanceOf[(t1.type, t2.type) => t3.type],
        transaction,
        methods,
        constraints,
        async,
        controller)
    }
    else if (action.isInstanceOf[(_, _, _) => _]) {
      val t4 = manifest[F].typeArguments(3)
      val t3 = manifest[F].typeArguments(2)
      val t2 = manifest[F].typeArguments(1)
      val t1 = manifest[F].typeArguments(0)
      new Action3[t1.type, t2.type, t3.type, t4.type, (t1.type, t2.type, t3.type) => t4.type](
        path,
        view,
        action.asInstanceOf[(t1.type, t2.type, t3.type) => t4.type],
        transaction,
        methods,
        constraints,
        async,
        controller)
    }
    else if (action.isInstanceOf[(_, _, _, _) => _]) {
      val t5 = manifest[F].typeArguments(4)
      val t4 = manifest[F].typeArguments(3)
      val t3 = manifest[F].typeArguments(2)
      val t2 = manifest[F].typeArguments(1)
      val t1 = manifest[F].typeArguments(0)
      new Action4[t1.type, t2.type, t3.type, t4.type, t5.type, (t1.type, t2.type, t3.type, t4.type) => t5.type](
        path,
        view,
        action.asInstanceOf[(t1.type, t2.type, t3.type, t4.type) => t5.type],
        transaction,
        methods,
        constraints,
        async,
        controller)
    }
    else
      throw new RuntimeException("To many arguments, only 4 arguments are allowed currently")
  }

  /**
   * An Action that takes no arguments
   */
  class Action0[R, F <: Function0[R]:Manifest](
          val path: String,
          val view: Direction,
          val action: F,
          val trans: Transaction,
          val methods:Seq[HttpMethod],
          val constrs: Seq[Constraint],
          val async:Boolean,
          val controller: Controller)
          extends Action {
    def returnType: AnyRef = manifest[F].typeArguments(0)

    def argTypes: Array[Class[_]] = Array.empty[Class[_]]

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
  class Action1[A, R, F <: Function1[A, R]:Manifest](
          val path: String,
          val view: Direction,
          val action: F,
          val trans: Transaction,
          val methods:Seq[HttpMethod],
          val constrs: Seq[Constraint],
          val async:Boolean,
          val controller: Controller)
          extends Action {
    def returnType: AnyRef = manifest[F].typeArguments(1)

    def argTypes: Array[Class[_]] = toClassList(manifest[F].typeArguments.slice(0, 1))

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
  class Action2[A1, A2, R, F <: Function2[A1, A2, R]:Manifest](
          val path: String,
          val view: Direction,
          val action: F,
          val trans: Transaction,
          val methods:Seq[HttpMethod],
          val constrs: Seq[Constraint],
          val async:Boolean,
          val controller: Controller)
          extends Action {
    def returnType: AnyRef = manifest[F].typeArguments(2)

    def argTypes: Array[Class[_]] = toClassList(manifest[F].typeArguments.slice(0, 2))

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
  class Action3[A1, A2, A3, R, F <: Function3[A1, A2, A3, R]:Manifest](
          val path: String,
          val view: Direction,
          val action: F,
          val trans: Transaction,
          val methods:Seq[HttpMethod],
          val constrs: Seq[Constraint],
          val async:Boolean,
          val controller: Controller)
          extends Action  {
    def returnType: AnyRef = manifest[F].typeArguments(3)
    def argTypes: Array[Class[_]] = toClassList(manifest[F].typeArguments.slice(0, 3))

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

  class Action4[A1, A2, A3, A4, R, F <: Function4[A1, A2, A3, A4, R]:Manifest](
          val path: String,
          val view: Direction,
          val action: F,
          val trans: Transaction,
          val methods:Seq[HttpMethod],
          val constrs: Seq[Constraint],
          val async:Boolean,
          val controller: Controller)
          extends Action {
    def returnType: AnyRef = manifest[F].typeArguments(4)

    def argTypes: Array[Class[_]] = toClassList(manifest[F].typeArguments.slice(0, 4))

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

  protected def toClassList(t: List[Manifest[_]]) = {
    t.map(t => {
      if (t.toString().endsWith(".type"))
        Class.forName(t.toString().substring(0, t.toString().length - 5))
      else
        Class.forName(t.toString())
    }).toArray
  }
}
