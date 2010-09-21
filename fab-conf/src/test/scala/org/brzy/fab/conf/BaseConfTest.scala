package org.brzy.fab.conf

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._


class BaseConfTest extends JUnitSuite {
  @Test def testCreateBase = {
    val conf = new BaseConf(Yaml(getClass.getResourceAsStream("/test.pluginDependencies.b.yml")))
    assertTrue(conf != null)
    assert(conf.dependencies.isDefined)
    assertEquals(3, conf.dependencies.get.size)
    assertTrue(conf.repositories.isDefined)
    assertEquals(1, conf.repositories.get.size)
  }

  @Test def testMergeBaseConfs = {
    val c1 = new BaseConf(Yaml(getClass.getResourceAsStream("/test.pluginDependencies.b.yml")))
    val c2 = new BaseConf(Yaml(getClass.getResourceAsStream("/test2.pluginDependencies.b.yml")))
    val c3 = c1 << c2

    assertTrue(c3 != null)
    assert(c3.dependencies.isDefined)
    assertEquals(4, c3.dependencies.get.size)
    assertTrue(c3.repositories.isDefined)
    assertEquals(1, c3.repositories.get.size)
  }
}