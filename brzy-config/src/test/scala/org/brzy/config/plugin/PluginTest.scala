package org.brzy.plugin

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.config.plugin.Plugin

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class PluginTest extends JUnitSuite {

  @Test
  def testBasicSet = {
    def plugin = new Plugin(Map("name"->"app",
      "version"->"1.0.0"))
    assertNotNull(plugin)
    assertNotNull(plugin.name.get)
    assertEquals("app",plugin.name.get)
    assertFalse(plugin.org.isDefined)
  }
}