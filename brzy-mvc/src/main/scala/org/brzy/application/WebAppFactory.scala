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
 * @version $Id: $
 */
object WebAppFactory {

  def create(configUrl:URL, env:String):WebApp = {
    val bootConfig = ConfigFactory.makeBootConfig(new File(configUrl.getFile), env)
    val view:Plugin = null
    val persistence:List[Plugin] = Nil
    val plugins:List[Plugin] = Nil
    val config = ConfigFactory.makeWebAppConfig(bootConfig, view, persistence, plugins)
    val c = Class.forName(config.application.applicationClass.get)
    val constructor = c.getConstructor(classOf[WebAppConfig])
		val inst = constructor.newInstance(config)
		inst.asInstanceOf[WebApp]
  }
}