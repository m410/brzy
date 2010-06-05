package org.brzy.application

import java.io.File
import org.brzy.webapp.ConfigFactory._
import java.net.URL
import org.brzy.config.plugin.Plugin
import org.brzy.config.webapp.WebAppConfig
import org.slf4j.LoggerFactory

/**
 * Creates the web application class from the configuration.
 *
 * @author Michael Fortin
 * @version $Id : $
 */
object WebAppFactory {

  private val log = LoggerFactory.getLogger(getClass)
  
  /**
   *
   */
  def create(configUrl: URL, env: String): WebApp = {
    val bootConfig = makeBootConfig(new File(configUrl.getFile), env)

    val view: Plugin = bootConfig.views match {
      case Some(v) =>
        if(v != null)
          makePlugin(bootConfig.views.get, fileForPlugin(bootConfig.views.get))
        else
          null
      case _ => null
    }

    val persistence: List[Plugin] = {
      bootConfig.persistence.get.map(p => {
        val file = fileForPlugin(p)
        makePlugin(p, file)
      })
    }
    val plugins: List[Plugin] = {
      bootConfig.plugins.get.map(p => {
        val file = fileForPlugin(p)
        makePlugin(p, file)
      })
    }
    val config = makeWebAppConfig(bootConfig, view, persistence, plugins)
    val c = Class.forName(config.application.applicationClass.get)
    val constructor = c.getConstructor(classOf[WebAppConfig])
    val inst = constructor.newInstance(config)
    inst.asInstanceOf[WebApp]
  }
}