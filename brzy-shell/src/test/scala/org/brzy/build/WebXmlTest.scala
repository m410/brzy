package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config.WebappConfig
import collection.mutable.ArrayBuffer


/**
 * @author Michael Fortin
 * @version $Id : $
 */
class WebXmlTest {

  @Test
  def testCreate = {
    val config = new WebappConfig(Map[String, AnyRef](
      "web_xml" -> List[AnyRef](
        Map[String, String]("session-config" -> "20"),
        Map[String, AnyRef]("context-param" -> Map[String, String](
          "param-name" -> "name",
          "param-value" -> "value"))
        )
      ))
    val webxml = new WebXml(config)
    println(webxml.body)
    assertNotNull(webxml.body)
  }
}