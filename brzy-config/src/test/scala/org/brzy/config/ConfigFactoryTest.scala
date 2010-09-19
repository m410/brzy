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
package org.brzy.config

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import java.io.File
import org.brzy.config.mod.Mod
import org.brzy.util.FileUtils._
import org.junit.{Ignore, Test}


class ConfigFactoryTest extends JUnitSuite {

  @Test def testRuntimeConfig = {
    val url = new File(getClass.getClassLoader.getResource("brzy-webapp.b.yml").getFile)
    val config = ConfigFactory.makeBootConfig(url, "development")

    assertNotNull(config)
    assertNotNull(config.application.get.applicationClass.get)
    assertNotNull("org.brzy.sample.WebApp", config.application.get.applicationClass.get)
    assertNotNull(config.application.get.version.get)
    assertNotNull(config.application.get.name.get)
    assertEquals("Test app", config.application.get.name.get)
    assertNotNull(config.application.get.author.get)
    assertNotNull(config.application.get.description.get)
    assertNotNull(config.application.get.org.get)
    assertNotNull(config.application.get.artifactId.get)
    assertNotNull(config.application.get.webappContext.get)
    assertNotNull(config.dependencies.get)
    assertNotNull(config.persistence.get)
    assertEquals(1, config.persistence.get.size)
    assertNotNull(config.logging.get)
    assertNotNull(config.logging.get.appenders.get)
    assertEquals(2, config.logging.get.appenders.get.size)
    assertNotNull(config.dependencies.get)
    assertNotNull(config.dependencyExcludes.get)

    assertNotNull(config.modules.get)
    assertEquals(1, config.modules.get.size)

    assertEquals(16, config.dependencies.get.size)
    assertEquals(1, config.dependencyExcludes.get.size)
    assertEquals(12, config.webXml.get.size)
  }


  @Test @Ignore def testDownload = {
    val workDir = new File(new File(System.getProperty("java.io.tmpdir")), "junitwork")

    if (workDir.exists)
      workDir.trash()

    workDir.mkdirs
    assertTrue(workDir.exists)

    val map = Map[String, AnyRef](
      "name" -> "brzy-scalate",
      "org" -> "org.brzy.scalate",
      "version" -> "0.1",
      "remote_location" -> "/Users/m410/Projects/brzy/brzy-config/src/test/resources/brzy-test-module.zip")
    val mod = new Mod(map)
    assertNotNull(mod)
    assertEquals(0, workDir.listFiles.length)
    ConfigFactory.installModule(workDir, mod)
    assertEquals(1, workDir.listFiles.length)
  }
}