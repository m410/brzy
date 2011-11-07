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
  @Test def testCreateConfiguration() {
    val wac = WebAppConfiguration.runtime(env = "test", defaultConfig = "/brzy-webapp.test.b.yml")
    assertNotNull(wac)
    assertTrue(wac.application.isDefined)
    assertTrue(wac.build.isDefined)
    assertTrue(wac.logging.isDefined)
    assertNotNull(wac.logging.get.loggers)
    assertNotNull(wac.logging.get.appenders)
    assertNotNull(wac.logging.get.root)
    assertEquals(1, wac.logging.get.root.get.ref.size)
    assertNotNull(wac.dependencies)
    assertEquals(16, wac.dependencies.size)
    assertNotNull(wac.dependencyExcludes)
    assertEquals(0, wac.dependencyExcludes.size)
    assertNotNull(wac.repositories)
    assertEquals(8, wac.repositories.size)
    assertNotNull(wac.webXml)
    assertNotNull(wac.environment)
    assertEquals(9, wac.webXml.size)

    assertEquals(1, wac.logging.get.root.get.ref.size)
    assertEquals("STDOUT", wac.logging.get.root.get.ref.get(0))
  }

  @Test def testJson() {
    val wac = WebAppConfiguration.runtime(env = "test", defaultConfig = "/brzy-webapp.test.b.yml")
    val json = wac.toJson
    assertNotNull(json)
    val wacFromJson = WebAppConfiguration.fromJson(json)
    assertNotNull(wacFromJson)
    
    assertTrue(wacFromJson.application.isDefined)
    assertTrue(wacFromJson.build.isDefined)
    assertNotNull(wacFromJson.logging.isDefined)
    assertTrue(wacFromJson.logging.get.loggers.isDefined)
    assertTrue(wacFromJson.logging.get.appenders.isDefined)
    assertTrue(wacFromJson.logging.get.root.isDefined)
    assertEquals(1, wacFromJson.logging.get.root.get.ref.size)
    assertNotNull(wacFromJson.dependencies)
    assertEquals(16, wacFromJson.dependencies.size)
    assertNotNull(wacFromJson.dependencyExcludes)
    assertEquals(0, wacFromJson.dependencyExcludes.size)
    assertNotNull(wacFromJson.repositories)
    assertEquals(8, wacFromJson.repositories.size)
    assertNotNull(wacFromJson.webXml)
    assertNotNull(wacFromJson.environment)
    assertEquals(9, wacFromJson.webXml.size)

    assertEquals(1, wacFromJson.logging.get.root.get.ref.size)
    assertEquals("STDOUT", wacFromJson.logging.get.root.get.ref.get(0))
  }
}
