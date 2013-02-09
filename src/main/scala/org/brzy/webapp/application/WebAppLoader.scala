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
package org.brzy.webapp.application

import org.slf4j.LoggerFactory

/**
 * Need to subclass to be able to dynamically load the application for development mode.
 *
 * @author Michael Fortin
 */
class WebAppLoader {
  private val log = LoggerFactory.getLogger(getClass)

  def load: WebApp = {
    val applicationLoader = getClass.getClassLoader
    LoggerFactory.getLogger(getClass).debug("app loader classloader={}",applicationLoader)

    try {
      val config = WebAppConfiguration.runtime("development")
      val projectApplicationClass = config.application.get.applicationClass.get

      val appClass = applicationLoader.loadClass(projectApplicationClass)
      val constructor = appClass.getConstructor(Array(classOf[WebAppConfiguration]): _*)
      constructor.newInstance(Array(config): _*).asInstanceOf[WebApp]
    }
    catch {
      case e:Throwable =>
        log.error("Could not load application because: " + e.getMessage, e)
        throw e // will become a reflection error above, but logging here anyway to get better
        // details in the stack trace, it gets truncated above.
    }
  }
}