package org.brzy.controller

import org.brzy.action.{Action, Constraint}
import org.brzy.persistence.Transaction
import org.brzy.action.response.{NoView, Direction}
import org.brzy.action.HttpMethod._
import org.brzy.persistence.Transaction

/**
 * This is the root trait for all Controllers.
 * 
 * @author Michael Fortin
 */
trait Controller {

  /**
   * The base of the path expression used to select an action.
   */
  val basePath: String

  /**
   * The constraints for the controller.  Constraints set here apply to all actions.  When
   * you add a constraint to the action is takes precedence over these.
   */
  val constraints: Seq[Constraint]

  /**
   * The default transaction for all actions defined in an instance of a controller.
   */
  val transaction: Transaction

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
}
