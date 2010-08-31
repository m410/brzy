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
package org.brzy.module

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.config.mod.Mod


class ModTest extends JUnitSuite {

  @Test
  def testBasicSet = {
    def mod = new Mod(Map("name"->"app",
      "version"->"1.0.0"))
    assertNotNull(mod)
    assertNotNull(mod.name.get)
    assertEquals("app",mod.name.get)
    assertFalse(mod.org.isDefined)
  }
}