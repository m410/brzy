package org.brzy.build

import org.junit.Test
import org.junit.Assert._
import org.brzy.config.{Dependency, AppConfig}

/**
 * http://stackoverflow.com/questions/2199040/scala-xml-building-adding-children-to-existing-nodes
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class IvyTest {

  @Test
  def testParsingXml = {
    val config = new AppConfig()
    val dep1 = new Dependency()
    dep1.conf= "compile"
    dep1.org = "org.package"
    dep1.name = "dep"
    dep1.rev = "1.0.0"

    val dep2 = new Dependency()
    dep2.conf = "compile"
    dep2.org = "org.apache"
    dep2.name = "dep2"
    dep2.rev = "1.0.1"

    config.dependencies = Array(dep1, dep2)
    
    val ivy = new IvyXml(config)
    val xml = ivy.body
    assertNotNull(xml)
  }
}