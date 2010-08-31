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
package org.brzy.shell

import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.brzy.config.mod.Mod
import org.brzy.webapp.ConfigFactory
import org.scalatest.junit.JUnitSuite



class WebXmlTest extends JUnitSuite {

  @Test
  def testCreate = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val bootConfig = ConfigFactory.makeBootConfig(new File(url.getFile), "test")
    val view: Mod = null
    val persistence: List[Mod] = Nil
    val modules: List[Mod] = Nil
    val config = ConfigFactory.makeWebAppConfig(bootConfig, view, persistence, modules)
    
    val webxml = new WebXml(config)
    println(webxml.body)
    assertNotNull(webxml.body)
  }
}