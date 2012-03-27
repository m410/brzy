package org.brzy.test

import org.brzy.application.{WebApp, WebAppConfiguration}


class ApplicationLoader {
  def load:WebApp = {
    val config = WebAppConfiguration.runtime("developement")
    val projectApplicationClass = config.application.get.applicationClass.get
    val applicationLoader = getClass.getClassLoader
    val appClass = applicationLoader.loadClass(projectApplicationClass)
    val constructor = appClass.getConstructor(Array(classOf[WebAppConfiguration]):_*)
    constructor.newInstance(Array(config):_*).asInstanceOf[WebApp]
  }
}