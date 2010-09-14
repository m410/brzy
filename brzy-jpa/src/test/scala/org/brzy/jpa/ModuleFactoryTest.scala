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
package org.brzy.jpa

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.brzy.config.mod.Mod
import org.brzy.config.ConfigFactory


class ModuleFactoryTest extends JUnitSuite {

  @Test def testAssemble = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    assertNotNull(url)
    val bootConfig = ConfigFactory.makeBootConfig(new File(url.getFile), "development")
    assertNotNull(bootConfig)
    bootConfig.persistence.get.foreach( p => {
      val unit = p.map("persistence_unit")
      assertNotNull(unit)
      assertEquals("my-unit",unit)
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
      val jpaModConfig = p.asInstanceOf[JpaModConfig]
      val unit = jpaModConfig.persistenceUnit.get
      assertNotNull(unit)
      assertEquals("my-unit",unit)
    })
  }
}