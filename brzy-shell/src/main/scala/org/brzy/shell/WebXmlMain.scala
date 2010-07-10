package org.brzy.shell

import xml.XML
import org.brzy.webapp.ConfigFactory
import java.io.File
import org.brzy.config.mod.Mod

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
object WebXmlMain {
  def main(args: Array[String]) = {
    println("[0]config = " + args(0))
    println("[1]env = " + args(1))
    println("[2]destination = " + args(2))
    val projectDir = new File("project")
    val modsDir = new File(projectDir,"brzy-modules")
    val bootConfig = ConfigFactory.makeBootConfig(new File(args(0)), args(1))

    val view: Mod = bootConfig.views match {
      case Some(v) =>
        if (v != null)
          ConfigFactory.makeBuildTimeModule(bootConfig.views.get,modsDir)
        else
          null
      case _ => null
    }

    val persistence: List[Mod] = {
      if (bootConfig.persistence.isDefined)
        bootConfig.persistence.get.map(ConfigFactory.makeBuildTimeModule(_,modsDir))
      else
        Nil
    }
    val modules: List[Mod] = {
      if (bootConfig.modules.isDefined)
        bootConfig.modules.get.map(ConfigFactory.makeBuildTimeModule(_,modsDir))
      else
        Nil
    }
    val config = ConfigFactory.makeWebAppConfig(bootConfig, view, persistence, modules)
    val parent = new File(args(2))
    val file = new File(parent,"web.xml")
    XML.save(file.getAbsolutePath, new WebXml(config).body)
  }
}