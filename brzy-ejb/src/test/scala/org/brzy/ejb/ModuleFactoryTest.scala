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
package org.brzy.ejb

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.application.WebAppConf

class ModuleFactoryTest extends JUnitSuite {
  @Test def testAssemble = {
    val conf = WebAppConf("test")
    assertNotNull(conf.modules)
    assertEquals(1, conf.modules.size)
    conf.modules.foreach(p => {
      val email = p.asInstanceOf[EjbModConfig]
      val host = email.jndi.get
      assertNotNull(host)
      assertEquals("localhost", host)
    })
  }
}