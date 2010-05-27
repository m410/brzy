package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config.{Dependency, WebappConfig}
import collection.mutable.ArrayBuffer

/**
 * http://stackoverflow.com/questions/2199040/scala-xml-building-adding-children-to-existing-nodes
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class IvyTest {
  @Test
  def testParsingXml = {
    val config = new WebappConfig(Map[String, AnyRef](
      "dependencies" -> List[AnyRef](
        Map[String, String](
          "conf" -> "compile",
          "org" -> "org.package",
          "name" -> "dep",
          "rev" -> "1.0.0"),
        Map[String, String](
          "conf" -> "compile",
          "org" -> "org.package",
          "name" -> "dep2",
          "rev" -> "1.0.1")
        )
      ))

    val ivy = new IvyXml(config)
    val xml = ivy.body
    assertNotNull(xml)
  }
}