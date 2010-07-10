package org.brzy.module

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.config.mod.Mod

/**
 * @author Michael Fortin
 * @version $Id: $
 */
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