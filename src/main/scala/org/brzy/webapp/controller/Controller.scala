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
package org.brzy.webapp.controller

import org.brzy.webapp.action.{Constraint, Action}
import org.brzy.webapp.action.response.{NoView, Direction}
import org.brzy.webapp.action.HttpMethod._
import org.brzy.webapp.persistence.Transaction

/**
 * Base class for all controllers.
 *
 * @author Michael Fortin
 */
trait Controller extends Ordered[Controller] {

  def basePath: String

  /**
   * The constraints for the controller.  Constraints set here apply to all actions.  When
   * you add a constraint to the action is takes precedence over these.
   */
  def constraints: Seq[Constraint]

  def transaction: Transaction

  /**
   * List of actions for this controller.  When overriding this, it's adventages to make it
   * a val so you the app doesn't make a list every time.
   */
  def actions: List[Action]


  def action[F <: AnyRef:Manifest](
          expr: String,
          act: F,
          view: Direction = NoView,
          transaction: Transaction = transaction,
          methods: Seq[HttpMethod] = Seq(GET,POST),
          constraints: Seq[Constraint] = constraints): Action = {
    Action(expr, act, transaction, methods, view, constraints, false, this)
  }

  def post[F <: AnyRef:Manifest](
          expr: String,
          act: F,
          view: Direction = NoView,
          transaction: Transaction = transaction,
          constraints: Seq[Constraint] = constraints): Action = {
    Action(expr, act, transaction, Seq(POST), view, constraints, false, this)
  }

  def get[F <: AnyRef:Manifest](
          expr: String,
          act: F,
          view: Direction = NoView,
          transaction: Transaction = transaction,
          methods: Seq[HttpMethod] = Seq.empty[HttpMethod],
          constraints: Seq[Constraint] = constraints): Action = {
    Action(expr, act, transaction, Seq(GET), view, constraints,  false, this)
  }

  def async[F <: AnyRef:Manifest](
          expr: String,
          act: F,
          view: Direction = NoView,
          transaction: Transaction = transaction,
          constraints: Seq[Constraint] = constraints): Action = {
    Action(expr, act, transaction, Seq(GET), view, constraints,  true, this)
  }

  /**
   * Compares the basePath of this controller to others.
   */
  def compare(that: Controller) = basePath.compareTo(that.basePath)
}