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
package org.brzy.application

import org.junit.Assert._

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.brzy.webapp.mock.MockModConfig


class WebAppTest extends JUnitSuite {

  @Test def testCreate = {
    val view = new MockModConfig(Map[String, AnyRef](
      "name" -> "brzy-scalate",
      "fileExtension" -> ".ssp",
      "resource_class" -> "org.brzy.webapp.mock.MockModResource"
      ))
//    val boot = new BootConfig(Map[String, AnyRef](
//      "environment" -> "development",
//      "application" -> Map(
//        "name" -> "test",
//        "org" -> "org.brzy.webapp.mock")
//      ))
//    val config = new WebAppConfig(boot, view, Nil, Nil)
    val webapp = null.asInstanceOf[WebApp]// new WebApp(config)

    assertNotNull(webapp)

    assertNotNull(webapp.serviceMap)
    assertEquals(1, webapp.serviceMap.size)

    assertNotNull(webapp.controllers)
    assertEquals(2, webapp.controllers.size)

    assertNotNull(webapp.actions)
    assertEquals(17, webapp.actions.size)
  }
}