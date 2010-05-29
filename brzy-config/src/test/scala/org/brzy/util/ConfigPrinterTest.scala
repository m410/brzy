package org.brzy.config

import org.junit.Test
import org.junit.Assert._
import java.util.{Map => JMap}
import org.ho.yaml.Yaml
import collection.JavaConversions._
import org.brzy.util.NestedCollectionConverter._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ConfigPrinterTest {

  @Test
  def testPrint() = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new WebappConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    ConfigPrinter(app)
    assertTrue(true)
  }

  @Test
  def testPrintDefault() = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.default.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new WebappConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    ConfigPrinter(app)
    assertTrue(true)
  }
}