package org.brzy.shell

import xml.XML
import org.brzy.webapp.ConfigFactory
import java.io.File
import org.brzy.config.plugin.Plugin

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
    val pluginsDir = new File(projectDir,"brzy-plugins")
    
    val bootConfig = ConfigFactory.makeBootConfig(new File(args(0)), args(1))

    val view: Plugin = bootConfig.views match {
      case Some(v) =>
        if (v != null)
          ConfigFactory.makeBuildTimePlugin(bootConfig.views.get,pluginsDir)
        else
          null
      case _ => null
    }

    val persistence: List[Plugin] = {
      if (bootConfig.persistence.isDefined)
        bootConfig.persistence.get.map(ConfigFactory.makeBuildTimePlugin(_,pluginsDir))
      else
        Nil
    }
    val plugins: List[Plugin] = {
      if (bootConfig.plugins.isDefined)
        bootConfig.plugins.get.map(ConfigFactory.makeBuildTimePlugin(_,pluginsDir))
      else
        Nil
    }
    val config = ConfigFactory.makeWebAppConfig(bootConfig, view, persistence, plugins)
    val parent = new File(args(2))
    val file = new File(parent,"web.xml")
    XML.save(file.getAbsolutePath, new WebXml(config).body)
  }
}