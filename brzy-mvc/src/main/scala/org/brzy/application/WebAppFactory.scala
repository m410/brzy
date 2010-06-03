package org.brzy.application

import java.io.File
import org.brzy.webapp.ConfigFactory
import java.net.URL
import org.brzy.config.plugin.Plugin
import org.brzy.config.webapp.WebAppConfig

/**
 * Creates the web application class from the configuration.
 *
 * @author Michael Fortin
 * @version $Id : $
 */
object WebAppFactory {

  /**
   *
   */
  def create(configUrl: URL, env: String): WebApp = {
    val bootConfig = ConfigFactory.makeBootConfig(new File(configUrl.getFile), env)
    val viewFile = ConfigFactory.fileForPlugin(bootConfig.views.get)
    val view: Plugin = ConfigFactory.makePlugin(bootConfig.views.get, viewFile)
    val persistence: List[Plugin] = {
      bootConfig.persistence.get.map(p => {
        val file = ConfigFactory.fileForPlugin(p)
        ConfigFactory.makePlugin(p, file)
      })
    }
    val plugins: List[Plugin] = {
      bootConfig.plugins.get.map(p => {
        val file = ConfigFactory.fileForPlugin(p)
        ConfigFactory.makePlugin(p, file)
      })
    }
    val config = ConfigFactory.makeWebAppConfig(bootConfig, view, persistence, plugins)
    val c = Class.forName(config.application.applicationClass.get)
    val constructor = c.getConstructor(classOf[WebAppConfig])
    val inst = constructor.newInstance(config)
    inst.asInstanceOf[WebApp]
  }
}