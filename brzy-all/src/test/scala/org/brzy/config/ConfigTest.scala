package org.brzy.config

import org.junit.Test
import org.junit.Assert._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ConfigTest {

  @Test
  def testInitApp = {
    val config = new Config
    config.application_class ="org.brzy.mock.MockWebApp"
    assertNotNull(config.initApp)
  }

  @Test
  def testMerge = {
    val config = new Config
    config.name = "test"
    config.version = "1.1.1"

    val config2 = new Config
    config2.name = "bob"

    val config3 = config2.merge(config)
    assertNotNull(config3)
    assertEquals(config2, config3)
    assertEquals("1.1.1", config3.version)
    assertEquals("test", config3.name)
  }
}