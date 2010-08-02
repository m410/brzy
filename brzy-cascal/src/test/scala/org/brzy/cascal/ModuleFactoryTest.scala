package org.brzy.cascal

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
    bootConfig.persistence.get.foreach( p => {
      val keySpace = p.map("key_space")
      assertNotNull(keySpace)
      assertEquals("Keyspace1",keySpace)
    })
    val persistence: List[Mod] = {
      if (bootConfig.persistence.isDefined)
        bootConfig.persistence.get.map( ConfigFactory.makeRuntimeModule(_))
      else
        Nil
    }
    assertNotNull(persistence)
    assertEquals(1,persistence.size)
    persistence.foreach( p => {
      val cascal = p.asInstanceOf[CascalModConfig]
      assertEquals("Keyspace1", cascal.keySpace.get)
      assertEquals(3,cascal.dependencies.get.size)
    })
  }
}