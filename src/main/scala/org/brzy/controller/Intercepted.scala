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

import org.brzy.action.args.{Arg, Principal}


/**
 * Adds action interceptor support to a controller. When added to any controller, the intercept
 * method wraps the action.
 *
 * @author Michael Fortin
 */
trait Intercepted {

  /**
   * Wraps all the actions calls for the controller.
   *
   * @param action This is a function that wraps the actual function the action points too.
   * @param actionArgs The arguments to the action.
   * @param principal The Principal.  This is here for use in fine grained authorization.
   *
   * @return The result of the call to the action argument.
   */
  def intercept(action: () => AnyRef, actionArgs: Array[Arg], principal: Principal): AnyRef = action()
}