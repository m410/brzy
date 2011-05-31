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

import org.brzy.webapp.action.Action

/**
 * Base class for all controllers.
 *
 * @author Michael Fortin
 */
abstract class Controller(val basePath:String) extends Ordered[Controller] {

  /**
   * Umm, don't remember.
   */
  implicit def selfReference = this

  /**
   * List of actions for this controller
   */
  def actions:List[Action]

  /**
   * Compares the basePath
   */
  def compare(that: Controller) = basePath.compareTo(that.basePath)
}