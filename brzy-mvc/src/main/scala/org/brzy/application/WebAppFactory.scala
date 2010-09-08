/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.application

import java.io.File
import org.brzy.config.ConfigFactory._
import java.net.URL
import org.brzy.config.mod.Mod
import org.brzy.config.webapp.WebAppConfig
import org.slf4j.LoggerFactory
import org.brzy.reflect.Construct

/**
 * Creates the web application class from the configuration.
 *
 * @author Michael Fortin
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
          makeRuntimeModule(bootConfig.views.get)
        else
          null
      case _ => null
    }

    val persistence: List[Mod] = {
      if (bootConfig.persistence.isDefined)
        bootConfig.persistence.get.map( makeRuntimeModule(_))
      else
        Nil
    }
    val modules: List[Mod] = {
      if (bootConfig.modules.isDefined)
        bootConfig.modules.get.map( makeRuntimeModule(_))
      else
        Nil
    }
    val config = makeWebAppConfig(bootConfig, view, persistence, modules)
    log.debug("application class: {}",config.application.applicationClass.get)

    if(config.application.applicationClass.isDefined)
      Construct[WebApp](config.application.applicationClass.get, Array(config))
    else
      new WebApp(config)
  }
}