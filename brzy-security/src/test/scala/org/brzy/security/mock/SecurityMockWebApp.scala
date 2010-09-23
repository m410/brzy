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
package org.brzy.security.mock

import collection.immutable.SortedSet
import org.brzy.webapp.action.Action
import org.brzy.application.{WebAppConf, WebApp}


class SecurityMockWebApp(config:WebAppConf) extends WebApp(config) {
  override def makeServiceMap = Map()
  override def makeControllers = List(new PersonController)
  override lazy val actions = SortedSet(
    new Action("/persons/{id}", classOf[PersonController].getMethods.find(_.getName == "show").get, new PersonController, ".ssp")
    )
}