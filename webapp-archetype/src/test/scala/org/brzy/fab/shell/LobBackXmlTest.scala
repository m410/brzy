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
package org.brzy.fab.shell


import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.config.webapp.WebAppConfig
import java.io.{File=>JFile}
import org.brzy.config.ConfigFactory
import org.brzy.fab.file.File


class LobBackXmlTest extends JUnitSuite {
  @Test def testCreateXml = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val boot = ConfigFactory.makeBootConfig(new JFile(url.getFile), "test")
    val config = new WebAppConfig(boot, null, Nil, Nil)
    val logbackxml = new LogBackXml(config)
    logbackxml.saveToFile(File("logback.xml").getAbsolutePath)
  }
}