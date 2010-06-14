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

class WebXmlMain {
  def main(args: Array[String]) {
    println("[0]config = " + args(0))
    println("[1]env = " + args(1))
    println("[2]destination = " + args(2))

    val bootConfig = ConfigFactory.makeBootConfig(new File(args(0)), args(1))

    val view: Plugin = bootConfig.views match {
      case Some(v) =>
        if (v != null)
          ConfigFactory.makeRuntimePlugin(bootConfig.views.get)
        else
          null
      case _ => null
    }

    val persistence: List[Plugin] = {
      if (bootConfig.persistence.isDefined)
        bootConfig.persistence.get.map(ConfigFactory.makeRuntimePlugin(_))
      else
        Nil
    }
    val plugins: List[Plugin] = {
      if (bootConfig.plugins.isDefined)
        bootConfig.plugins.get.map(ConfigFactory.makeRuntimePlugin(_))
      else
        Nil
    }
    val config = ConfigFactory.makeWebAppConfig(bootConfig, view, persistence, plugins)
    XML.save(args(3), new WebXml(config).body)
  }
}