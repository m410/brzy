package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config.{WebXmlNode, AppConfig}


/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebXmlTest {

  @Test
  def testCreate = {
    val config = new AppConfig()
    val hashmap = new java.util.HashMap[String,java.lang.Object]()
    hashmap.put("session-config","20")

    val inmap = new java.util.HashMap[String,java.lang.Object]()
    inmap.put("param-name","name")
    inmap.put("param-value","value")
    hashmap.put("context-param",inmap)
    config.web_xml = hashmap
    val webxml = new WebXml(config)
		println(webxml.body)
    assertNotNull(webxml.body)
  }
}