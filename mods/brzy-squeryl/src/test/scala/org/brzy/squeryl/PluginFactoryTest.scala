package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import java.io.File
import org.brzy.config.mod.Mod
import org.brzy.webapp.ConfigFactory

class PluginFactoryTest extends JUnitSuite {

  @Test
  def testAssemble = {
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    assertNotNull(url)
    val bootConfig = ConfigFactory.makeBootConfig(new File(url.getFile), "development")
    assertNotNull(bootConfig)
    bootConfig.persistence.get.foreach( p => {
      val driver = p.map("driver")
      assertNotNull(driver)
      assertEquals("org.postgresql.Driver",driver)
    })
    val persistence: List[Mod] = {
      if (bootConfig.persistence.isDefined)
        bootConfig.persistence.get.map( ConfigFactory.makeRuntimePlugin(_))
      else
        Nil
    }
    assertNotNull(persistence)
    assertEquals(1,persistence.size)
    persistence.foreach( p => {
      val squeryl = p.asInstanceOf[SquerylPluginConfig]
      val driver = squeryl.driver.get
      assertNotNull(driver)
      assertEquals("org.postgresql.Driver",driver)
    })
  }
}