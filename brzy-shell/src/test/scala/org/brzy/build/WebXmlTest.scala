package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config.WebappConfig


/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebXmlTest {

  @Test
  def testCreate = {
    val config = new WebappConfig()
    val list = new java.util.ArrayList[java.util.HashMap[String,java.lang.Object]]()
    val hashmap = new java.util.HashMap[String,java.lang.Object]()
    hashmap.put("session-config","20")
    list.add(hashmap)

    val hashmap2 = new java.util.HashMap[String,java.lang.Object]()
    val inmap = new java.util.HashMap[String,java.lang.Object]()
    inmap.put("param-name","name")
    inmap.put("param-value","value")
    hashmap2.put("context-param",inmap)
    list.add(hashmap2)
    config.webXml = list
    val webxml = new WebXml(config)
		println(webxml.body)
    assertNotNull(webxml.body)
  }
}