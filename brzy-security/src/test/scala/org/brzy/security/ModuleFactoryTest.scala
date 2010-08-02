package org.brzy.security

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.brzy.config.mod.Mod
import org.brzy.webapp.ConfigFactory

class ModuleFactoryTest extends JUnitSuite {

  @Test def testAssemble = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    assertNotNull(url)
    val bootConfig = ConfigFactory.makeBootConfig(new File(url.getFile), "development")
    assertNotNull(bootConfig)

    bootConfig.modules.get.foreach(p => {
      val authority = p.map("authority_entity")
      assertNotNull(authority)
      assertEquals("Person", authority)
    })
    val modules: List[Mod] = {
      if (bootConfig.modules.isDefined)
        bootConfig.modules.get.map(ConfigFactory.makeRuntimeModule(_))
      else
        Nil
    }
    assertNotNull(modules)
    assertEquals(1, modules.size)
    modules.foreach(p => {
      val securityModConfig = p.asInstanceOf[SecurityModConfig]
      val authorityEntity = securityModConfig.authorityEntity.get
      assertNotNull(authorityEntity)
      assertEquals("Person", authorityEntity)
    })
  }
}