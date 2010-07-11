package org.brzy.config

import common.BootConfig
import org.junit.Test
import org.junit.Assert._
import java.util.{Map => JMap}
import org.ho.yaml.Yaml
import collection.JavaConversions._
import org.brzy.util.NestedCollectionConverter._
import org.scalatest.junit.JUnitSuite


class ConfigPrinterTest extends JUnitSuite {

  @Test
  def testPrint() = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new BootConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    ConfigPrinter(app)
    assertTrue(true)
  }

  @Test
  def testPrintDefault() = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.default.b.yml")
    val config = Yaml.load(url.openStream)
    config.asInstanceOf[JMap[String, AnyRef]].put("environment", "development")
    val app = new BootConfig(convertMap(config.asInstanceOf[JMap[String, AnyRef]]))
    ConfigPrinter(app)
    assertTrue(true)
  }
}