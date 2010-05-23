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
    assertNotNull(app.environment.get)
    assertNull(app.project)
    assertEquals("development", app.environment.get)
    assertNotNull(app.application.get)
    assertNotNull(app.application.get.version.get)
    assertEquals("1.0.0", app.application.get.version.get)
    assertNotNull(app.application.get.name.get)
    assertEquals("Test app", app.application.get.name.get)
    assertNotNull(app.application.get.author.get)
    assertEquals("Fred", app.application.get.author.get)
    assertNotNull(app.application.get.description.get)
    assertEquals("Some Description", app.application.get.description.get)
    assertNotNull(app.application.get.org.get)
    assertEquals("org.brzy.sample", app.application.get.org.get)
    assertNotNull(app.application.get.artifactId.get)
    assertEquals("sample-jpa", app.application.get.artifactId.get)
    assertNotNull(app.application.get.applicationClass.get)
    assertEquals("org.brzy.mock.MockWebApp", app.application.get.applicationClass.get)
    assertNotNull(app.application.get.webappContext.get)
    assertEquals("mainapp", app.application.get.webappContext.get)
    assertNotNull(app.dependencies.get)
    assertEquals(2, app.dependencies.get.size)
    assertNotNull(app.repositories.get)
    assertEquals(1, app.repositories.get.size)
    assertNotNull(app.plugins.get)
    assertEquals(1, app.plugins.get.size)
    assertNotNull(app.persistence.get)
    assertEquals(1, app.persistence.get.size)
    assertNotNull(app.logging.get)
    assertNotNull(app.logging.get.appenders.get)
    assertEquals(2, app.logging.get.appenders.get.size)
    assertNotNull(app.logging.get.loggers)
    assertEquals(1, app.logging.get.loggers.size)
    assertNotNull(app.logging.get.root)
    assertNotNull(app.webXml.get)
    assertEquals(4, app.webXml.get.size)
  }

  @Test
  def testLoadDefault = {
    val url = getClass.getClassLoader.getResource("brzy-app.default.b.yml")
    val config = Yaml.load(url.openStream)
    val app = new WebappConfig(config.asInstanceOf[JMap[String, AnyRef]].toMap)
    assertNotNull(app)
    assertNotNull(app.project.get)
    assertNull(app.application.get)
    assertNotNull(app.dependencies.get)
    assertEquals(17, app.dependencies.get.size)
    assertNotNull(app.repositories.get)
    assertEquals(4, app.repositories.get.size)
    assertNull(app.plugins.get)
    assertNull(app.persistence.get)
    assertNull(app.logging.get)
    assertNotNull(app.webXml.get)
    assertEquals(10, app.webXml.get.size)
  }

  @Test
  def testMerge = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val webapp = new WebappConfig(config.asInstanceOf[JMap[String, AnyRef]].toMap)
    assertNotNull(webapp)
    assertNotNull(webapp.dependencies.get)
    assertEquals(2, webapp.dependencies.get.size)

    val url2 = getClass.getClassLoader.getResource("brzy-app.default.b.yml")
    val config2 = Yaml.load(url2.openStream)
    val defaultConfig = new WebappConfig(config2.asInstanceOf[JMap[String, AnyRef]].toMap)
    assertNotNull(defaultConfig)
    assertNotNull(defaultConfig.dependencies.get)
    assertEquals(17, defaultConfig.dependencies.get.size)

    val merged = webapp << defaultConfig

    assertNotNull(merged.project.get)
    assertEquals("development", merged.environment.get)
    assertNotNull(merged.application.get)
    assertNotNull(merged.repositories.get)
    assertEquals(5, merged.repositories.get.size)
    assertNotNull(merged.plugins.get)
    assertEquals(1, merged.plugins.get.size)
    assertNotNull(merged.persistence.get)
    assertEquals(1, merged.persistence.get.size)
    assertNotNull(merged.logging.get)
    assertNotNull(merged.logging.get.appenders.get)
    assertEquals(2, merged.logging.get.appenders.size)
    assertNotNull(merged.logging.get.loggers.get)
    assertEquals(1, merged.logging.get.loggers.get.size)
    assertNotNull(merged.logging.get.root)
    assertNotNull(merged.webXml.get)
    assertEquals(14, merged.webXml.get.size)
    assertNotNull(merged.dependencies.get)
    assertEquals(19, merged.dependencies.get.size)
  }

}