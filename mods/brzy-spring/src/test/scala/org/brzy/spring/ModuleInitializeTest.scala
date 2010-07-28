package org.brzy.spring

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite


class ModuleInitializeTest extends JUnitSuite {
  @Test def testConTextSetup = {
    val mod = new SpringModResource(new SpringModConfig(Map(
        "application_context" -> "applicationContext.xml",
        "name" -> "brzy-sping"
      )))
    assertEquals(1,mod.services.size)
  }
}