package org.brzy.application

import org.brzy.config.AppConfig

/**
 * Creates the web application class from the configuration.
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
object WebAppFactory {

  def create(config:AppConfig):WebApp = {
    val c = Class.forName(config.application.application_class)
    val constructor = c.getConstructor(classOf[AppConfig])
		val inst = constructor.newInstance(config)
		inst.asInstanceOf[WebApp]
  }
}