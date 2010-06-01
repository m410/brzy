package org.brzy.application

import org.brzy.config.BootConfig

/**
 * Creates the web application class from the configuration.
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
object WebAppFactory {

  def create(config:BootConfig):WebApp = {
    val c = Class.forName(config.application.get.applicationClass.get)
    val constructor = c.getConstructor(classOf[BootConfig])
		val inst = constructor.newInstance(config)
		inst.asInstanceOf[WebApp]
  }
}