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

import org.brzy.webapp.action.args.{Principal, Arg}
import org.brzy.webapp.action.response.{Flash, Redirect}


/**
 * Marks a controller as a secure controller using role based authentication.
 * 
 * @author Michael Fortin
 */
trait Authorization extends Intercepted { self:Controller =>

  /**
   * Wraps all the actions calls for the controller.  If the user is not authenticated
   * they are redirected to the login page.
   *
   * @param action This is a function that wraps the actual function the action points too.
   * @param actionArgs The arguments to the action.
   * @param principal The Principal.  This is here for use in fine grained authorization.
   *
   * @return The result of the call to the action argument.
   */
  override def intercept(action: () => AnyRef, actionArgs: Array[Arg], principal: Principal) = {
    if (principal.isLoggedIn)
      super.intercept(action, actionArgs, principal)
    else
      (Redirect("/auth"),Flash("Your session has ended. Please login again", "session.end"))
  }
}