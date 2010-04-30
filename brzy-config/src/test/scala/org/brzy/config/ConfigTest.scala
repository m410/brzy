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
    val overrideConfig = new AppConfig
    overrideConfig.application = new org.brzy.config.Application
		overrideConfig.environment = "app"
    overrideConfig.application.name = "test"
    overrideConfig.application.version = "1.1.1"

    val defaultConfig = new AppConfig
    defaultConfig.application = new org.brzy.config.Application
    defaultConfig.application.name = "bob"

    val mergedConfig = defaultConfig + overrideConfig
    assertNotNull(mergedConfig)
    assertTrue(defaultConfig != mergedConfig)
    assertEquals("1.1.1", mergedConfig.application.version)
    assertEquals("test", mergedConfig.application.name)
  }

  @Test
  def testMerge3 = {
    val subConfig = new AppConfig
    subConfig.application = new org.brzy.config.Application
    subConfig.application.version = "1.1.2"
    subConfig.application.artifact_id = "tester"

    val appConfig = new AppConfig
    appConfig.application = new org.brzy.config.Application
		appConfig.environment = "app"
    appConfig.application.version = "1.1.1"
    appConfig.application.group_id = "org.group"

    val defaultConfig = new AppConfig
    defaultConfig.application = new org.brzy.config.Application
    defaultConfig.application.name = "bob"

    val mergedConfig = defaultConfig + appConfig + subConfig
    assertNotNull(mergedConfig)
    assertTrue(defaultConfig != mergedConfig)
    assertEquals("1.1.2", mergedConfig.application.version)
    assertEquals("bob", mergedConfig.application.name)
    assertEquals("tester", mergedConfig.application.artifact_id)
    assertEquals("org.group", mergedConfig.application.group_id)
    assertNull( mergedConfig.application.webapp_context)
  }
}