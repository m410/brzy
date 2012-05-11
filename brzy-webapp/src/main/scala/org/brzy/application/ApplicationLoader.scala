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

import org.slf4j.LoggerFactory

/**
 * Need to subclass to be able to dynamcally load the application for developement mode.
 *
 * @author Michael Fortin
 */
class ApplicationLoader {
  def load: WebApp = {
    val applicationLoader = getClass.getClassLoader
    LoggerFactory.getLogger(getClass).debug("app loader classloader={}",applicationLoader)

    val config = WebAppConfiguration.runtime("development")
    val projectApplicationClass = config.application.get.applicationClass.get

    val appClass = applicationLoader.loadClass(projectApplicationClass)
    val constructor = appClass.getConstructor(Array(classOf[WebAppConfiguration]): _*)
    constructor.newInstance(Array(config): _*).asInstanceOf[WebApp]
  }
}