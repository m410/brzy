package org.brzy.exp

import org.brzy.webapp.action.Constraint

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
abstract class Controller(val basePath:String) extends Ordered[Controller] {

  /**
   * The constraints for the controller.  Constraints set here apply to all actions.  When
   * you add a constraint to the action is takes precedence over these.
   */
  val constraints:List[Constraint] = List.empty[Constraint]

  val defaultTransaction:Transaction = new Transaction()

  /**
   * Umm, not sure why this is needed, but the CrudController won't compile without it.
   */
  implicit def selfReference = this

  /**
   * List of actions for this controller.  When overriding this, it's adventages to make it
   * a val so you the app doesn't make a list every time.
   */
  val actions:List[Action] = List.empty[Action]


  /**
   * This can be used to create an action does does nothing but draw a view.
   */
  def inaction() {}


  /**
   * Compares the basePath of this controller to others.
   */
  def compare(that: Controller) = basePath.compareTo(that.basePath)
}