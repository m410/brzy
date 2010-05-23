package org.brzy.application

import org.brzy.config.WebappConfig

/**
 * Creates the web application class from the configuration.
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
object WebAppFactory {

  def create(config:WebappConfig):WebApp = {
    val c = Class.forName(config.application.get.applicationClass.get)
    val constructor = c.getConstructor(classOf[WebappConfig])
		val inst = constructor.newInstance(config)
		inst.asInstanceOf[WebApp]
  }
}