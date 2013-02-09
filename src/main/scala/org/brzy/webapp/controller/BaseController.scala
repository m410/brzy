package org.brzy.webapp.controller

import org.brzy.webapp.action.{Action, Constraint}
import org.brzy.webapp.persistence.Transaction

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class BaseController(val basePath: String) extends Controller {

  val constraints: Seq[Constraint] = Seq.empty[Constraint]

  val transaction: Transaction = Transaction()

  def actions: List[Action] = List.empty[Action]

}
