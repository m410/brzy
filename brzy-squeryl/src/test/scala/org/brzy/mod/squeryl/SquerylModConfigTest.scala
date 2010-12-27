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
import org.brzy.fab.mod.Mod


class SquerylModConfigTest extends JUnitSuite {
  @Test
  def testMerge = {
    def mod = new Mod(Map(
      "name" -> "brzy-squeryl",
      "org" -> "orb.grzy",
      "version" -> "0.2",
      "driver" -> "org.h2.Driver",
      "user_name" -> "sa",
      "password" -> "",
      "url" -> "jdbc:h2:test_db",
      "adaptor_name" -> "h2"))
    assertNotNull(mod)
    assertEquals("brzy-squeryl", mod.name.get)

    def squeryl = new SquerylModConfig(Map(
      "name" -> "brzy-squeryl",
      "org" -> "orb.grzy"))
    assertNotNull(squeryl)
    assertEquals("brzy-squeryl", squeryl.name.get)
    assertFalse(squeryl.driver.isDefined)

    val merged = {squeryl << mod}.asInstanceOf[SquerylModConfig]
    assertNotNull(merged)
    assertTrue(merged.driver.isDefined)
    assertEquals("org.h2.Driver", merged.driver.get)
  }
}