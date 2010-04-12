package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config.{Dependency, Config}

/**
 * http://stackoverflow.com/questions/2199040/scala-xml-building-adding-children-to-existing-nodes
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class IvyTest {

  @Test
  def testParsingXml = {
    val config = new Config()
    val dep1 = new Dependency()
    dep1.lib = "compile:org.package:dep:1.0.0"
    val dep2 = new Dependency()
    dep2.lib = "compile:org.apache:dep2:1.0.1"
    config.dependencies = Array(dep1, dep2)
    
    val ivy = new IvyXml(config)
    val xml = ivy.body
    assertNotNull(xml)
  }
}