package org.brzy.jsp

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
      val host = p.map("application_context")
      assertNotNull(host)
      assertEquals("localhost", host)
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
      val email = p.asInstanceOf[JspModConfig]
      val host = email.applicationContext.get
      assertNotNull(host)
      assertEquals("localhost", host)
    })
  }
}