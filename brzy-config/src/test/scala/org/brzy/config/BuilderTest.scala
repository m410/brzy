package org.brzy.config

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._

/**
 *
 * @author Michael Fortin
 * @version $Id: $
 */
class BuilderTest {

  val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
  
	@Test
	def testApplicationConfig = {
    val config = new Builder(url,"development").applicationConfig
    assertNotNull(config)
    assertNotNull(config.application.version)
    assertNotNull(config.application.name)
    assertEquals("Test app", config.application.name)
    assertNotNull(config.application.author)
    assertNotNull(config.application.description)
    assertNotNull(config.application.group_id)
    assertNotNull(config.application.artifact_id)
    assertNotNull(config.logging)
    assertNotNull(config.persistence)
	}
	
	@Test
	def testDefaultConfig = {
    val config = new Builder(url,"development").defaultConfig
		assertNotNull(config)
		assertNotNull(config.dependencies)
		assertTrue(config.dependencies.length > 0)
    assertNotNull(config.repositories)
    assertTrue(config.repositories.length > 0)
	}
	
	@Test
	def testEnvironmentConfigDev = {
    val config  = new Builder(url, "development").environmentConfig
    assertNotNull(config)
    assertEquals("devapp",config.application.webapp_context)
	}

  @Test
	def testEnvironmentConfigTest = {
    val config  = new Builder(url, "test").environmentConfig
    assertNotNull(config)
    assertEquals("testapp",config.application.webapp_context)
	}

  @Test
	def testEnvironmentConfigProd = {
    val config  = new Builder(url, "production").environmentConfig
    assertNotNull(config)
    assertEquals("prodapp",config.application.webapp_context)
	}

  @Test
	def testPluginConfig = {
    val builder: Builder = new Builder(url, "development")
    val configs = builder.pluginConfigs
		assertNotNull(configs)
    assertEquals(3,configs.size)
	}

  @Test
  def testRuntimeConfig = {
    val config = new Builder(url,"development").runtimeConfig
    assertNotNull(config)
    assertNotNull(config.application.application_class)
    assertNotNull("org.brzy.sample.WebApp",config.application.application_class)
    assertNotNull(config.application.version)
    assertNotNull(config.application.name)
    assertEquals("Test app", config.application.name)
    assertNotNull(config.application.author)
    assertNotNull(config.application.description)
    assertNotNull(config.application.group_id)
    assertNotNull(config.application.artifact_id)
    assertNotNull(config.application.webapp_context)
    assertNotNull(config.dependencies)
    assertNotNull(config.persistence)
    assertEquals(2,config.persistence.size)
    assertNotNull(config.logging)
    assertNotNull(config.logging.appenders)
    assertEquals(2, config.logging.appenders.size)
    assertNotNull(config.web_xml)
//    assertNotNull(config.environment_overrides)
//    assertEquals(3,config.environment_overrides.size)
//    assertEquals(false,config.environment_overrides(0).)

//    assertNotNull(config.plugins)
//    assertEquals(4,config.plugins.size)
    assertNotNull(config.application.properties)
    assertEquals(1,config.application.properties.size)
  }
}