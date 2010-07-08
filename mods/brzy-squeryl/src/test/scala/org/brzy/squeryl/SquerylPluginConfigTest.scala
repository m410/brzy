package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.config.plugin.Plugin

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class SquerylPluginConfigTest extends JUnitSuite {
  @Test
  def testMerge = {
    def plugin = new Plugin(Map(
      "name" -> "brzy-squeryl",
      "org" -> "orb.grzy",
      "version" -> "0.2",
      "driver" -> "org.h2.Driver",
      "user_name" -> "sa",
      "password" -> "",
      "url" -> "jdbc:h2:test_db",
      "adaptor_name" -> "h2"))
    assertNotNull(plugin)
    assertEquals("brzy-squeryl", plugin.name.get)

    def squeryl = new SquerylPluginConfig(Map(
      "name" -> "brzy-squeryl",
      "org" -> "orb.grzy"))
    assertNotNull(squeryl)
    assertEquals("brzy-squeryl", squeryl.name.get)
    assertFalse(squeryl.driver.isDefined)

    val merged = {squeryl << plugin}.asInstanceOf[SquerylPluginConfig]
    assertNotNull(merged)
    assertTrue(merged.driver.isDefined)
    assertEquals("org.h2.Driver", merged.driver.get)
  }
}