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


class WebAppTest extends JUnitSuite {

  @Test def testCreateWebApp() {
    val webappConf = WebAppConfiguration.runtime(env="test",defaultConfig="/brzy-webapp.test.b.yml")
    assertNotNull(webappConf)

    val webapp = WebApp(webappConf)
    assertNotNull(webapp)

    assertNotNull(webapp.services)
    assertEquals(1, webapp.services.size)

    assertNotNull(webapp.controllers)
    assertEquals(2, webapp.controllers.size)

    assertNotNull(webapp.actions)
    assertEquals(19, webapp.actions.size)
  }
}