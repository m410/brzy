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
package org.brzy.webapp.action

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.web.{MockServletConfig, MockServletContext, MockHttpServletResponse, MockHttpServletRequest}
import org.brzy.application.WebApp
import org.brzy.fab.conf.webapp.WebAppConfig
import org.brzy.fab.conf.common.BootConfig
import org.brzy.webapp.mock.MockModConfig

class ServletTest extends JUnitSuite {
  @Test def testPath = {
    val view = new MockModConfig(Map[String, AnyRef](
      "name" -> "brzy-scalate",
      "fileExtension" -> ".ssp",
      "resource_class" -> "org.brzy.webapp.mock.MockModResource"
      ))
    val boot = new BootConfig(Map[String, AnyRef](
      "environment" -> "development",
      "application" -> Map(
        "name" -> "test",
        "org" -> "org.brzy.webapp.mock")
      ))
    val config = new WebAppConfig(boot, view, Nil, Nil)
    val webapp = new WebApp(config)
    assertNotNull(webapp)
    assertEquals(2,webapp.controllers.size)
    assertEquals(17,webapp.actions.size)

    val request = new MockHttpServletRequest("GET", "//users.brzy")
    val response = new MockHttpServletResponse()

    val context = new MockServletContext()
    context.setAttribute("application",webapp)

    val servlet = new Servlet()
    servlet.init(new MockServletConfig(context))
    servlet.service(request,response)
    assertEquals(200,response.getStatus)
  }


  @Test def testPathWithParam = {
    val view = new MockModConfig(Map[String, AnyRef](
      "name" -> "brzy-scalate",
      "fileExtension" -> ".ssp",
      "resource_class" -> "org.brzy.webapp.mock.MockModResource"
      ))
    val boot = new BootConfig(Map[String, AnyRef](
      "environment" -> "development",
      "application" -> Map(
        "name" -> "test",
        "org" -> "org.brzy.webapp.mock")
      ))
    val config = new WebAppConfig(boot, view, Nil, Nil)
    val webapp = new WebApp(config)
    assertNotNull(webapp)
    assertEquals(2,webapp.controllers.size)
    assertEquals(17,webapp.actions.size)

    val request = new MockHttpServletRequest("GET", "//users/10.brzy")
    val response = new MockHttpServletResponse()

    val context = new MockServletContext()
    context.setAttribute("application",webapp)

    val servlet = new Servlet()
    servlet.init(new MockServletConfig(context))
    servlet.service(request,response)
    assertEquals(200,response.getStatus)
  }
}