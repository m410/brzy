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
package org.brzy.mod.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.application.WebAppConfiguration

class ModuleFactoryTest extends JUnitSuite {

  @Test def testAssemble() {
    val config = WebAppConfiguration.runtime("test")
    assertNotNull(config.persistence)
    assertEquals(1,config.persistence.size)
    config.persistence.foreach( p => {
      val squeryl = p.asInstanceOf[SquerylModConfig]
      val driver = squeryl.driver.get
      assertNotNull(driver)
      assertEquals("org.postgresql.Driver",driver)
    })
  }
}