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
    val node = new WebXmlNode()
    node.name = "session-config"
    node.content ="20"

    val node3  = new WebXmlNode()
    node3.name = "context-param"

    val node4  = new WebXmlNode()
    node4.name = "param-name"
    node4.content = "name"

    val node5  = new WebXmlNode()
    node5.name = "param-value"
    node5.content = "value"

    node3.children = Array(node4,node5)
    
    config.web_xml = Array(node,node3)
    val webxml = new WebXml(config)
    assertNotNull(webxml.body)
  }
}