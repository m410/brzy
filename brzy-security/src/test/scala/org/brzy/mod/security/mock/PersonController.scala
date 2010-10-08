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
package org.brzy.mod.security.mock

import org.brzy.mod.security.Secured
import org.brzy.webapp.action.args.Parameters
import org.brzy.webapp.controller.{Action, Controller}


@Controller("persons")
@Secured(Array("ROLE_USER","ROLE_ADMIN"))
class PersonController {

  @Action("") def list =  {}
  @Action("{id}") def show(p:Parameters) = "person"->Person.get(p("id"))

}