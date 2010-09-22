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
import org.junit.Test

import org.scalatest.junit.JUnitSuite

class WebAppConfTest extends JUnitSuite {

  @Test def testCreate = {
    val webappConf = WebAppConf(env="test",defaultConfig="/brzy-webapp.test.b.yml")
    assertNotNull(webappConf)
    assertNotNull(webappConf.application)
    assertNotNull(webappConf.project)
    assertNotNull(webappConf.logging)
    assertNotNull(webappConf.logging.loggers)
    assertNotNull(webappConf.logging.appenders)
    assertNotNull(webappConf.logging.root)
    assertEquals(1,webappConf.logging.root.get.ref.size)
    assertEquals("STDOUT",webappConf.logging.root.get.ref.get(0))
    assertNotNull(webappConf.dependencies)
    assertEquals(17,webappConf.dependencies.size)
    assertNotNull(webappConf.dependencyExcludes)
    assertEquals(0, webappConf.dependencyExcludes.size)
    assertNotNull(webappConf.repositories)
    assertEquals(8,webappConf.repositories.size)
    assertNotNull(webappConf.webXml)
    assertEquals(12,webappConf.webXml)
    assertNotNull(webappConf.environment)
  }
}