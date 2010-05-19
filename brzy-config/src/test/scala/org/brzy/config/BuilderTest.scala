package org.brzy.config

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import java.io.File
import org.brzy.util.FileUtils._
import org.brzy.plugin.ScalatePluginConfig

/**
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class BuilderTest {
  val url = getClass.getClassLoader.getResource("brzy-app.b.yml")

  @Test
  def testApplicationConfig = {
    val config = new Builder(url, "development").applicationConfig
    assertNotNull(config)
    assertNotNull(config.application.version)
    assertNotNull(config.application.name)
    assertEquals("Test app", config.application.name)
    assertNotNull(config.application.author)
    assertNotNull(config.application.description)
    assertNotNull(config.application.org)
    assertNotNull(config.application.artifactId)
    assertNotNull(config.logging)
    assertNotNull(config.persistence)
    assertNotNull(config.webXml)
  }

  @Test
  def testDefaultConfig = {
    val config = new Builder(url, "development").defaultConfig
    assertNotNull(config)
    assertNotNull(config.dependencies)
    assertTrue(config.dependencies.length > 0)
    assertNotNull(config.repositories)
    assertTrue(config.repositories.length > 0)
  }

  @Test
  def testEnvironmentConfigDev = {
    val builder = new Builder(url, "development")
    val config = builder.environmentConfig
    assertNotNull(config)
    assertEquals("devapp", config.application.webappContext)
  }

  @Test
  def testEnvironmentConfigTest = {
    val config = new Builder(url, "test").environmentConfig
    assertNotNull(config)
    assertEquals("testapp", config.application.webappContext)
  }

  @Test
  def testEnvironmentConfigProd = {
    val config = new Builder(url, "production").environmentConfig
    assertNotNull(config)
    assertEquals("prodapp", config.application.webappContext)
  }

  @Test
  def testPluginConfig = {
    val builder: Builder = new Builder(url, "development")
    val configs = builder.pluginConfigs
    assertNotNull(configs)
    assertEquals(1, configs.size)
  }

  @Test
  @Ignore
  def testRuntimeConfig = {
    val config = new Builder(url, "development").runtimeConfig

    //      assertNotNull(config)
    //      assertNotNull(config.application.applicationClass)
    //      assertNotNull("org.brzy.sample.WebApp",config.application.applicationClass)
    //      assertNotNull(config.application.version)
    //      assertNotNull(config.application.name)
    //      assertEquals("Test app", config.application.name)
    //      assertNotNull(config.application.author)
    //      assertNotNull(config.application.description)
    //      assertNotNull(config.application.groupId)
    //      assertNotNull(config.application.artifactId)
    //      assertNotNull(config.application.webappContext)
    //      assertNotNull(config.dependencies)
    //      assertNotNull(config.persistence)
    //      assertEquals(2,config.persistence.size)
    //      assertNotNull(config.logging)
    //      assertNotNull(config.logging.appenders)
    //      assertEquals(2, config.logging.appenders.size)
    //      assertEquals("webxml: " + config.webXml,19,config.webXml.size)
    //      assertNotNull(config.dependencies)
    //      assertEquals("dependencies: "+config.dependencies.mkString(", "),30,config.dependencies.length)
    //
    //      assertNotNull(config.environment_overrides)
    //      assertEquals(3,config.environment_overrides.size)
    //      assertEquals(false,config.environment_overrides(0).)
    //
    //      assertNotNull(config.plugins)
    //      assertEquals(4,config.plugins.size)
    //      assertNotNull(config.application.properties)
    //      assertEquals(1,config.application.properties.size)
  }

  @Test
  def testDownload() = {
    val builder = new Builder(url, "development")
    val workDir = new File(new File(System.getProperty("java.io.tmpdir")), "junitwork")

    if (workDir.exists)
      workDir.trash()

    workDir.mkdirs
    assertTrue(workDir.exists)

    val map = Map[String, AnyRef](
      "name" -> "brzy-scalate",
      "org" -> "org.brzy",
      "version" -> "0.1",
      "config_class" -> "org.brzy.plugin.ScalatePluginConfig",
      "file_extension" -> ".ssp",
      "html_version" -> "html5",
      "remote_location" -> "/Users/m410/Projects/brzy/brzy-config/src/test/resources/brzy-test-plugin.zip")
    val plugin = new ScalatePluginConfig(map)
    assertNotNull(plugin)
    assertEquals(0, workDir.listFiles.length)
    builder.downloadAndUnzipTo(plugin, workDir)
    assertEquals(1, workDir.listFiles.length)
  }
}