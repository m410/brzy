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
    assertNotNull(config.application.groupId)
    assertNotNull(config.application.artifactId)
    assertNotNull(config.logging)
    assertNotNull(config.persistence)
    assertNotNull(config.webXml)
	}
	
//	@Test
//	def testDefaultConfig = {
//    val config = new Builder(url,"development").defaultConfig
//		assertNotNull(config)
//		assertNotNull(config.dependencies)
//		assertTrue(config.dependencies.length > 0)
//    assertNotNull(config.repositories)
//    assertTrue(config.repositories.length > 0)
//	}
//
//	@Test
//	def testEnvironmentConfigDev = {
//    val config  = new Builder(url, "development").environmentConfig
//    assertNotNull(config)
//    assertEquals("devapp",config.application.webappContext)
//	}
//
//  @Test
//	def testEnvironmentConfigTest = {
//    val config  = new Builder(url, "test").environmentConfig
//    assertNotNull(config)
//    assertEquals("testapp",config.application.webappContext)
//	}
//
//  @Test
//	def testEnvironmentConfigProd = {
//    val config  = new Builder(url, "production").environmentConfig
//    assertNotNull(config)
//    assertEquals("prodapp",config.application.webappContext)
//	}
//
//  @Test
//	def testPluginConfig = {
//    val builder: Builder = new Builder(url, "development")
//    val configs = builder.pluginConfigs
//		assertNotNull(configs)
//    assertEquals(4,configs.size)
//	}
//
//  @Test
//  def testRuntimeConfig = {
//    val config = new Builder(url,"development").runtimeConfig
//    assertNotNull(config)
//    assertNotNull(config.application.applicationClass)
//    assertNotNull("org.brzy.sample.WebApp",config.application.applicationClass)
//    assertNotNull(config.application.version)
//    assertNotNull(config.application.name)
//    assertEquals("Test app", config.application.name)
//    assertNotNull(config.application.author)
//    assertNotNull(config.application.description)
//    assertNotNull(config.application.groupId)
//    assertNotNull(config.application.artifactId)
//    assertNotNull(config.application.webappContext)
//    assertNotNull(config.dependencies)
//    assertNotNull(config.persistence)
//    assertEquals(2,config.persistence.size)
//    assertNotNull(config.logging)
//    assertNotNull(config.logging.appenders)
//    assertEquals(2, config.logging.appenders.size)
//    assertEquals("webxml: " + config.webXml,19,config.webXml.size)
//    assertNotNull(config.dependencies)
//    assertEquals("dependencies: "+config.dependencies.mkString(", "),30,config.dependencies.length)
//
////    assertNotNull(config.environment_overrides)
////    assertEquals(3,config.environment_overrides.size)
////    assertEquals(false,config.environment_overrides(0).)
//
////    assertNotNull(config.plugins)
////    assertEquals(4,config.plugins.size)
//    assertNotNull(config.application.properties)
//    assertEquals(1,config.application.properties.size)
//  }
}