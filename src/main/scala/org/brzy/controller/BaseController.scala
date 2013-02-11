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
package org.brzy.controller

import org.brzy.webapp.action.{Constraint, Action}
import org.brzy.webapp.persistence.Transaction
import org.brzy.webapp.controller.Controller

/**
 * Base class for all controllers.
 *
 * @author Michael Fortin
 */
abstract class BaseController(val basePath: String) extends Controller {

  /**
   * The constraints for the controller.  Constraints set here apply to all actions.  When
   * you add a constraint to the action is takes precedence over these.
   */
  val constraints: Seq[Constraint] = Seq.empty[Constraint]

  val transaction: Transaction = Transaction()

  /**
   * List of actions for this controller.  When overriding this, it's adventages to make it
   * a val so you the app doesn't make a list every time.
   */
  def actions: List[Action] = List.empty[Action]

}