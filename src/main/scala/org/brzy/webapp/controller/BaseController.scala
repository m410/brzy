package org.brzy.webapp.controller

import org.brzy.webapp.action.{Action, Constraint}
import org.brzy.webapp.persistence.DefaultTransaction

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class BaseController(val basePath: String) extends Controller {

  val constraints  = Seq.empty[Constraint]

  val transaction = DefaultTransaction()

  def actions = List.empty[Action]

}
