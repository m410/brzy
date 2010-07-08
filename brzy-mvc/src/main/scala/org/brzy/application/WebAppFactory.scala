package org.brzy.application

import java.io.File
import org.brzy.webapp.ConfigFactory._
import java.net.URL
import org.brzy.config.mod.Mod
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

    val view: Mod = bootConfig.views match {
      case Some(v) =>
        if (v != null)
          makeRuntimePlugin(bootConfig.views.get)
        else
          null
      case _ => null
    }

    val persistence: List[Mod] = {
      if (bootConfig.persistence.isDefined)
        bootConfig.persistence.get.map( makeRuntimePlugin(_))
      else
        Nil
    }
    val plugins: List[Mod] = {
      if (bootConfig.plugins.isDefined)
        bootConfig.plugins.get.map( makeRuntimePlugin(_))
      else
        Nil
    }
    val config = makeWebAppConfig(bootConfig, view, persistence, plugins)
    log.debug("application class: {}",config.application.applicationClass.get)
    val c = Class.forName(config.application.applicationClass.get)
    val constructor = c.getConstructor(classOf[WebAppConfig])
    val inst = constructor.newInstance(config)
    inst.asInstanceOf[WebApp]
  }
}