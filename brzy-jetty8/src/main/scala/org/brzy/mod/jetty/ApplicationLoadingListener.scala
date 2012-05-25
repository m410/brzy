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
package org.brzy.mod.jetty

import javax.servlet.{ServletContextEvent, ServletContextListener}
import java.io.File
//import org.brzy.application.{ApplicationLoader, WebApp}
import java.net.{URL, URLClassLoader}
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter

@deprecated
class ApplicationLoadingListener(loaderClass:String, cp:Array[URL]) extends ServletContextListener {

  def contextInitialized(ctx: ServletContextEvent) {
    val lc = LoggerFactory.getILoggerFactory

    if (lc.isInstanceOf[LoggerContext])
      StatusPrinter.print(lc.asInstanceOf[LoggerContext])
    else {
      val factory = LoggerFactory.getILoggerFactory
      println("### LoggerFactory is instance of %s".format(factory.getClass.toString))
    }

    val applicationLoader = new URLClassLoader(cp, getClass.getClassLoader)
    java.lang.Thread.currentThread().setContextClassLoader(applicationLoader)

    LoggerFactory.getLogger(getClass).debug("this classloader={}",getClass.getClassLoader)
    LoggerFactory.getLogger(getClass).debug("app  classloader={}",applicationLoader)

    val appClass = applicationLoader.loadClass(loaderClass)
    val loaderInstance = appClass.newInstance()
    val method = appClass.getMethod("load")
    val webapp = method.invoke(loaderInstance)
    webapp.getClass.getMethod("startup").invoke(webapp)
    ctx.getServletContext.setAttribute("application", webapp)
  }

  def contextDestroyed(servletContextEvent: ServletContextEvent) {
    val app: Any = servletContextEvent.getServletContext.getAttribute("application")

//    if (app != null)
//      app.asInstanceOf[WebApp].shutdown()
  }
}