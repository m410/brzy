package org.brzy.fab.conf

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._


class ModTest extends JUnitSuite {
  @Test def testLoadMod = {
    val conf = new ViewMod(Yaml(getClass.getResourceAsStream("/test.brzy-module.b.yml")))
    assertTrue(conf != null)
    assert(conf.dependencies.isDefined)
    assertEquals(2, conf.dependencies.get.size)
    assertTrue(conf.repositories.isDefined)
    assertEquals(1, conf.repositories.get.size)

    assertTrue(conf.name.isDefined)
    assertTrue(conf.org.isDefined)
    assertTrue(conf.version.isDefined)
    assertTrue(conf.fileExtension.isDefined)
    assertTrue(conf.configClass.isDefined)
    assertTrue(conf.resourceClass.isDefined)
  }
}