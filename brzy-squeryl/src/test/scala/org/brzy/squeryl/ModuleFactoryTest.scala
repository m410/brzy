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
package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.brzy.config.mod.Mod
import org.brzy.webapp.ConfigFactory

class ModuleFactoryTest extends JUnitSuite {

  @Test
  def testAssemble = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    assertNotNull(url)
    val bootConfig = ConfigFactory.makeBootConfig(new File(url.getFile), "development")
    assertNotNull(bootConfig)
    bootConfig.persistence.get.foreach( p => {
      val driver = p.map("driver")
      assertNotNull(driver)
      assertEquals("org.postgresql.Driver",driver)
    })
    val persistence: List[Mod] = {
      if (bootConfig.persistence.isDefined)
        bootConfig.persistence.get.map( ConfigFactory.makeRuntimeModule(_))
      else
        Nil
    }
    assertNotNull(persistence)
    assertEquals(1,persistence.size)
    persistence.foreach( p => {
      val squeryl = p.asInstanceOf[SquerylModConfig]
      val driver = squeryl.driver.get
      assertNotNull(driver)
      assertEquals("org.postgresql.Driver",driver)
    })
  }
}