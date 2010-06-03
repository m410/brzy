package org.brzy.config.common

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import java.io.File
import org.ho.yaml.Yaml
import java.util.{Map => JMap}
import org.brzy.util.NestedCollectionConverter._
import org.junit.{Ignore, Test}
import org.brzy.webapp.ConfigFactory

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class BootConfigTest extends JUnitSuite {

  @Test
  def testBoot: Unit = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val file = new File(url.toURI.toURL.getFile)
    assertNotNull(file)
    ConfigFactory.makeBootConfig(file, "development")
    assertTrue(true)
  }

  @Test
  @Ignore
  def testInit: Unit = {
    val initFile = new File("/Users/m410/Projects/brzy/project/brzy-webapp.development.b.yml")
    assertTrue(initFile.exists)
    val jMap = Yaml.load(initFile).asInstanceOf[JMap[String, AnyRef]]
    val initConfig = new BootConfig(convertMap(jMap))
    assertNotNull(initConfig)
    assertNotNull(initConfig.application.get.applicationClass.get)
    assertNotNull("org.brzy.sample.WebApp", initConfig.application.get.applicationClass.get)
    assertNotNull(initConfig.application.get.version.get)
    assertNotNull(initConfig.application.get.name.get)
    assertEquals("Test app", initConfig.application.get.name.get)
    assertNotNull(initConfig.application.get.author.get)
    assertNotNull(initConfig.application.get.description.get)
    assertNotNull(initConfig.application.get.org.get)
    assertNotNull(initConfig.application.get.artifactId.get)
    assertNotNull(initConfig.application.get.webappContext.get)
    assertNotNull(initConfig.dependencies.get)
    assertNotNull(initConfig.persistence.get)
    assertEquals(1, initConfig.persistence.get.size)
    assertNotNull(initConfig.logging.get)
    assertNotNull(initConfig.logging.get.appenders.get)
    assertEquals(2, initConfig.logging.get.appenders.get.size)
    assertNotNull(initConfig.dependencies.get)
  }
}