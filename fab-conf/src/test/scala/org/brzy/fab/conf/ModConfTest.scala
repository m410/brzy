package org.brzy.fab.conf

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._


class ModConfTest extends JUnitSuite {
  @Test def testCreateMod = {
    val conf = new ModConf(Yaml(getClass.getResourceAsStream("/test.brzy-webapp.b.yml")))
    assertTrue(conf != null)
    assertTrue(conf.dependencies.isDefined)
    assertEquals(4, conf.dependencies.get.size)
    assertTrue(conf.repositories.isDefined)
    assertEquals(2, conf.repositories.get.size)
    assertTrue(conf.views.isDefined)
    assertTrue(conf.persistence.isDefined)
    assertEquals(1, conf.persistence.get.size)
    assertTrue(conf.modules.isDefined)
    assertEquals(1, conf.modules.get.size)
  }


  @Test def testMergeBaseConfs = {
    assert(true)
  }
}