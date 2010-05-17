package org.brzy.config

import org.junit.Test
import org.junit.Assert._
import java.util.{HashMap => JHashMap, Map => JMap}
import org.ho.yaml.Yaml
import collection.JavaConversions._

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class WebappConfigTest {


  @Test
  def testLoad = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new WebappConfig(config.asInstanceOf[JMap[String, AnyRef]].toMap)
    assertNotNull(app)
    assertNotNull(app.environment)
    assertNull(app.project)
    assertEquals("development", app.environment)
    assertNotNull(app.application)
    assertNotNull(app.application.version)
    assertEquals("1.0.0", app.application.version)
    assertNotNull(app.application.name)
    assertEquals("Test app", app.application.name)
    assertNotNull(app.application.author)
    assertEquals("Fred", app.application.author)
    assertNotNull(app.application.description)
    assertEquals("Some Description", app.application.description)
    assertNotNull(app.application.groupId)
    assertEquals("org.brzy.sample", app.application.groupId)
    assertNotNull(app.application.artifactId)
    assertEquals("sample-jpa", app.application.artifactId)
    assertNotNull(app.application.applicationClass)
    assertEquals("org.brzy.mock.MockWebApp", app.application.applicationClass)
    assertNotNull(app.application.webappContext)
    assertEquals("mainapp", app.application.webappContext)
    assertNotNull(app.dependencies)
    assertEquals(2, app.dependencies.size)
    assertNotNull(app.repositories)
    assertEquals(1, app.repositories.size)
    assertNotNull(app.plugins)
    assertEquals(1, app.plugins.size)
    assertNotNull(app.persistence)
    assertEquals(1, app.persistence.size)
    assertNotNull(app.logging)
    assertNotNull(app.logging.appenders)
    assertEquals(2, app.logging.appenders.size)
    assertNotNull(app.logging.loggers)
    assertEquals(1, app.logging.loggers.size)
    assertNotNull(app.logging.root)
    assertNotNull(app.webXml)
    assertEquals(2, app.webXml.size)
  }

  @Test
  def testLoadDefault = {
    val url = getClass.getClassLoader.getResource("brzy-app.default.b.yml")
    val config = Yaml.load(url.openStream)
    val app = new WebappConfig(config.asInstanceOf[JMap[String, AnyRef]].toMap)
    assertNotNull(app)
    assertNotNull(app.project)
    assertNull(app.application)
    assertNotNull(app.dependencies)
    assertEquals(17, app.dependencies.size)
    assertNotNull(app.repositories)
    assertEquals(4, app.repositories.size)
    assertNull(app.plugins)
    assertNull(app.persistence)
    assertNull(app.logging)
    assertNotNull(app.webXml)
    assertEquals(12, app.webXml.size)
  }

  @Test
  def testMerge = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val webapp = new WebappConfig(config.asInstanceOf[JMap[String, AnyRef]].toMap)
    assertNotNull(webapp)
    assertNotNull(webapp.dependencies)
    assertEquals(2, webapp.dependencies.size)

    val url2 = getClass.getClassLoader.getResource("brzy-app.default.b.yml")
    val config2 = Yaml.load(url2.openStream)
    val defaultConfig = new WebappConfig(config2.asInstanceOf[JMap[String, AnyRef]].toMap)
    assertNotNull(defaultConfig)
    assertNotNull(defaultConfig.dependencies)
    assertEquals(17, defaultConfig.dependencies.size)

    val merged = webapp + defaultConfig

    assertNotNull(merged.project)
    assertEquals("development", merged.environment)
    assertNotNull(merged.application)
//    assertEquals(2, merged.dependencies.size)
    assertNotNull(merged.repositories)
//    assertEquals(1, merged.repositories.size)
    assertNotNull(merged.plugins)
//    assertEquals(1, merged.plugins.size)
    assertNotNull(merged.persistence)
//    assertEquals(1, merged.persistence.size)
    assertNotNull(merged.logging)
    assertNotNull(merged.logging.appenders)
//    assertEquals(2, merged.logging.appenders.size)
    assertNotNull(merged.logging.loggers)
//    assertEquals(1, merged.logging.loggers.size)
    assertNotNull(merged.logging.root)
    assertNotNull(merged.webXml)
//    assertEquals(2, merged.webXml.size)
    assertNotNull(merged.dependencies)
    assertEquals(19, merged.dependencies.size)
  }

}