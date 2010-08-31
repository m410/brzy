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
package org.brzy.config.webapp

import org.junit.Test
import org.junit.Assert._
import java.util.{Map => JMap}
import org.ho.yaml.Yaml
import collection.JavaConversions._
import org.brzy.util.NestedCollectionConverter._
import org.scalatest.junit.JUnitSuite
import org.brzy.config.common.BootConfig


class WebAppConfigTest extends JUnitSuite {
  @Test def testLoad = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new BootConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    assertNotNull(app)
    assertNotNull(app.environment.get)
    assertTrue(app.project.isEmpty)
    assertEquals("development", app.environment.get)
    assertNotNull(app.application.get)
    assertNotNull(app.application.get.version.get)
    assertEquals("1.0.0", app.application.get.version.get)
    assertNotNull(app.application.get.name.get)
    assertEquals("Test app", app.application.get.name.get)
    assertNotNull(app.application.get.author.get)
    assertEquals("Fred", app.application.get.author.get)
    assertNotNull(app.application.get.description.get)
    assertEquals("Some Description", app.application.get.description.get)
    assertNotNull(app.application.get.org.get)
    assertEquals("org.brzy.sample", app.application.get.org.get)
    assertNotNull(app.application.get.artifactId.get)
    assertEquals("sample-jpa", app.application.get.artifactId.get)
    assertNotNull(app.application.get.applicationClass.get)
    assertEquals("org.brzy.mock.MockWebApp", app.application.get.applicationClass.get)
    assertNotNull(app.application.get.webappContext.get)
    assertEquals("mainapp", app.application.get.webappContext.get)
    assertNotNull(app.dependencies.get)
    assertEquals(2, app.dependencies.get.size)
    assertNotNull(app.repositories.get)
    assertEquals(1, app.repositories.get.size)
    assertNotNull(app.modules.get)
    assertEquals(1, app.modules.get.size)
    assertNotNull(app.persistence.get)
    assertEquals(1, app.persistence.get.size)
    assertNotNull(app.logging.get)
    assertNotNull(app.logging.get.appenders.get)
    assertEquals(2, app.logging.get.appenders.get.size)
    assertNotNull(app.logging.get.loggers)
    assertEquals(1, app.logging.get.loggers.size)
    assertNotNull(app.logging.get.root)
    assertNotNull(app.webXml.get)
    assertEquals(2, app.webXml.get.size)
  }

  @Test
  def testLoadDefault = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.default.b.yml")
    val config = Yaml.load(url.openStream)
    val app = new BootConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    assertNotNull(app)
    assertNotNull(app.project.get)
    assertTrue(app.application.isEmpty)
    assertNotNull(app.dependencies.get)
    assertEquals(11, app.dependencies.get.size)
    assertNotNull(app.repositories.get)
    assertEquals(5, app.repositories.get.size)
    assertTrue(app.modules.isEmpty)
    assertTrue(app.persistence.isEmpty)
    assertTrue(app.logging.isEmpty)
    assertNotNull(app.webXml.get)
    assertEquals(10, app.webXml.get.size)
  }

  @Test
  def testMerge = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val webapp = new BootConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    assertNotNull(webapp)
    assertNotNull(webapp.dependencies.get)
    assertEquals(2, webapp.dependencies.get.size)
    assertEquals(2, webapp.logging.get.appenders.get.size)
    assertEquals(1, webapp.logging.get.loggers.get.size)

    val url2 = getClass.getClassLoader.getResource("brzy-webapp.default.b.yml")
    val config2 = Yaml.load(url2.openStream)
    val defaultConfig = new BootConfig(convertMap(config2.asInstanceOf[JMap[String, AnyRef]]))
    assertNotNull(defaultConfig)
    assertNotNull(defaultConfig.dependencies.get)
    assertEquals(11, defaultConfig.dependencies.get.size)

    val merged = defaultConfig << webapp

    assertNotNull(merged.project.get)
    assertEquals("development", merged.environment.get)
    assertNotNull(merged.application.get)
    assertNotNull(merged.repositories.get)
    assertEquals(6, merged.repositories.get.size)
    assertNotNull(merged.modules.get)
    assertEquals(1, merged.modules.get.size)
    assertNotNull(merged.persistence.get)
    assertEquals(1, merged.persistence.get.size)
    assertNotNull(merged.logging.get)
    assertNotNull(merged.logging.get.appenders.get)
    assertEquals(2, merged.logging.get.appenders.get.size)
    assertNotNull(merged.logging.get.loggers.get)
    assertEquals(1, merged.logging.get.loggers.get.size)
    assertNotNull(merged.logging.get.root)
    assertNotNull(merged.webXml.get)
    assertEquals(12, merged.webXml.get.size)
    assertNotNull(merged.dependencies.get)
    assertEquals(13, merged.dependencies.get.size)
  }

}