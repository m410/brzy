package org.brzy.config

import org.junit.Test
import org.junit.Assert._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ConfigPrinterTest {

  @Test
  def testPrint() = {
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    val config = new Builder(url,"development").runtimeConfig
    ConfigPrinter(config)
    assertTrue(true)
  }
}