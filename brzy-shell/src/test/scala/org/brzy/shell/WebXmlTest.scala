package org.brzy.shell

import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.brzy.config.plugin.Plugin
import org.brzy.webapp.ConfigFactory
import org.scalatest.junit.JUnitSuite


/**
 * @author Michael Fortin
 * @version $Id : $
 */
class WebXmlTest extends JUnitSuite {

  @Test
  def testCreate = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    val bootConfig = ConfigFactory.makeBootConfig(new File(url.getFile), "test")
    val view: Plugin = null
    val persistence: List[Plugin] = Nil
    val plugins: List[Plugin] = Nil
    val config = ConfigFactory.makeWebAppConfig(bootConfig, view, persistence, plugins)
    
    val webxml = new WebXml(config)
    println(webxml.body)
    assertNotNull(webxml.body)
  }
}