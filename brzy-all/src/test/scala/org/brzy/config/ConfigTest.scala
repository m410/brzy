package org.brzy.config

import org.junit.Test
import org.junit.Assert._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ConfigTest {

  @Test
  def testMerge2 = {
    val appConfig = new Config
		appConfig.environment = "app"
		appConfig.config_type = "master"
    appConfig.name = "test"
    appConfig.version = "1.1.1"

    val defaultConfig = new Config
    defaultConfig.name = "bob"

    val mergedConfig = defaultConfig + appConfig
    assertNotNull(mergedConfig)
    assertTrue(defaultConfig != mergedConfig)
    assertEquals("1.1.1", mergedConfig.version)
    assertEquals("bob", mergedConfig.name)
  }

  @Test
  def testMerge3 = {
    val subConfig = new Config
    subConfig.name = "test deep"
    subConfig.version = "1.1.1"
    subConfig.artifact_id = "tester"

    val appConfig = new Config
		appConfig.environment = "app"
		appConfig.config_type = "master"
    appConfig.name = "test"
    appConfig.version = "1.1.1"
    appConfig.group_id = "org.group"

    val defaultConfig = new Config
    defaultConfig.name = "bob"

    val mergedConfig = defaultConfig + appConfig + subConfig
    assertNotNull(mergedConfig)
    assertTrue(defaultConfig != mergedConfig)
    assertEquals("1.1.1", mergedConfig.version)
    assertEquals("bob", mergedConfig.name)
    assertEquals("tester", mergedConfig.artifact_id)
    assertEquals("org.group", mergedConfig.group_id)
    assertNull( mergedConfig.webapp_context)
  }
}