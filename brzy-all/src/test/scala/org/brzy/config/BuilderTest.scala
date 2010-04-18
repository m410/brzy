package org.brzy.config

import org.junit.Test
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
    assertNotNull(config.version)
    assertNotNull(config.name)
    assertEquals("Test app", config.name)
    assertNotNull(config.author)
    assertNotNull(config.description)
    assertNotNull(config.group_id)
    assertNotNull(config.artifact_id)
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
    assertEquals("devapp",config.webapp_context)
	}

  @Test
	def testEnvironmentConfigTest = {
    val config  = new Builder(url, "test").environmentConfig
    assertNotNull(config)
    assertEquals("testapp",config.webapp_context)
	}

  @Test
	def testEnvironmentConfigProd = {
    val config  = new Builder(url, "production").environmentConfig
    assertNotNull(config)
    assertEquals("prodapp",config.webapp_context)
	}

  @Test
	def testPluginConfig = {
    val builder: Builder = new Builder(url, "development")
    val configs = builder.pluginConfigs
		assertNotNull(configs)
    assertEquals(6,configs.size)
	}

  @Test
  def testRuntimeConfig = {
    val config = new Builder(url,"development").runtimeConfig
    assertNotNull(config)
    assertNotNull(config.version)
    assertNotNull(config.name)
    assertEquals("Test app", config.name)
    assertNotNull(config.author)
    assertNotNull(config.description)
    assertNotNull(config.group_id)
    assertNotNull(config.artifact_id)
    assertNotNull(config.webapp_context)
    assertNotNull(config.db_migration)
    assertNotNull(config.src_package )
    assertNotNull(config.data_source)
    assertEquals("something",config.data_source.name)
    assertNotNull(config.dependencies)
    assertNotNull(config.persistence_properties)
    assertEquals(6,config.persistence_properties.size)
    assertNotNull(config.logging)
    assertNotNull(config.logging.appenders)
    assertEquals(2, config.logging.appenders.size)
    assertNotNull(config.web_xml)
    assertNotNull(config.environment_overrides)
    assertEquals(3,config.environment_overrides.size)
    assertEquals(false,config.environment_overrides(0).db_migration)

    assertNotNull(config.plugins)
    assertEquals(4,config.plugins.size)
    assertNotNull(config.application_properties)
    assertEquals(1,config.application_properties.size)
  }

	@Test
	def testWebApplication = {
		val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    assertNotNull(url)
    val app = new Builder(url,"development").webApplication
		assertNotNull(app)
	}
}