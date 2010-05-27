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
    assertNotNull(config.application.get.version.get)
    assertNotNull(config.application.get.name.get)
    assertEquals("Test app", config.application.get.name.get)
    assertNotNull(config.application.get.author.get)
    assertNotNull(config.application.get.description.get)
    assertNotNull(config.application.get.org.get)
    assertNotNull(config.application.get.artifactId.get)
    assertNotNull(config.logging.get)
    assertNotNull(config.persistence.get)
    assertNotNull(config.webXml.get)
  }

  @Test
  def testDefaultConfig = {
    val config = new Builder(url, "development").defaultConfig
    assertNotNull(config)
    assertNotNull(config.dependencies.get)
    assertTrue(config.dependencies.get.length > 0)
    assertNotNull(config.repositories.get)
    assertTrue(config.repositories.get.length > 0)
  }

  @Test
  def testEnvironmentConfigDev = {
    val builder = new Builder(url, "development")
    val config = builder.environmentConfig
    assertNotNull(config)
    assertEquals("devapp", config.application.get.webappContext.get)
  }

  @Test
  def testEnvironmentConfigTest = {
    val config = new Builder(url, "test").environmentConfig
    assertNotNull(config)
    assertEquals("testapp", config.application.get.webappContext.get)
  }

  @Test
  def testEnvironmentConfigProd = {
    val config = new Builder(url, "production").environmentConfig
    assertNotNull(config)
    assertEquals("prodapp", config.application.get.webappContext.get)
  }

  @Test
  def testPluginConfig = {
    val builder: Builder = new Builder(url, "development")
    val configs = builder.pluginConfigs
    assertNotNull(configs)
    assertEquals(1, configs.size)
  }

  @Test
  def testRuntimeConfig = {
    val config = new Builder(url, "development").runtimeConfig

    assertNotNull(config)
    assertNotNull(config.application.get.applicationClass.get)
    assertNotNull("org.brzy.sample.WebApp", config.application.get.applicationClass.get)
    assertNotNull(config.application.get.version.get)
    assertNotNull(config.application.get.name.get)
    assertEquals("Test app", config.application.get.name.get)
    assertNotNull(config.application.get.author.get)
    assertNotNull(config.application.get.description.get)
    assertNotNull(config.application.get.org.get)
    assertNotNull(config.application.get.artifactId.get)
    assertNotNull(config.application.get.webappContext.get)
    assertNotNull(config.dependencies.get)
    assertNotNull(config.persistence.get)
    assertEquals(1, config.persistence.get.size)
    assertNotNull(config.logging.get)
    assertNotNull(config.logging.get.appenders.get)
    assertEquals(2, config.logging.get.appenders.get.size)
    assertNotNull(config.dependencies.get)

    assertNotNull(config.plugins)
    assertEquals(0, config.plugins.size)

    assertEquals("dependencies: " + config.dependencies.mkString(", "), 30, config.dependencies.get.length)

    assertEquals("webxml: " + config.webXml, 19, config.webXml.size) 

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