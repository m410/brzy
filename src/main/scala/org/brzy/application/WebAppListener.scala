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

import javax.servlet.{ServletContextEvent, ServletContextListener}
import org.slf4j.LoggerFactory
import ch.qos.logback.core.util.StatusPrinter
import ch.qos.logback.classic.LoggerContext

/**
 * Web application listener placed in the web.xml to initialize and destroy the 
 * application.  This Loads the Webapp, and places it in the application context.
 *
 * @see http://www.softwareengineeringsolutions.com/blogs/2010/08/01/programmatic-definition-of-components-in-servlet-specification-3-0/
 * @author Michael Fortin
 */
//@WebListener
class WebAppListener extends ServletContextListener {

  private[this] val log = LoggerFactory.getLogger(classOf[WebAppListener])

  def contextInitialized(servletContextEvent: ServletContextEvent) {
    val lc = LoggerFactory.getILoggerFactory

    if(lc.isInstanceOf[LoggerContext])
      StatusPrinter.print(lc.asInstanceOf[LoggerContext])
    else {
      val factory = LoggerFactory.getILoggerFactory
      println("### LoggerFactory is instance of %s".format(factory.getClass.toString))
    }

    val env = servletContextEvent.getServletContext.getInitParameter("brzy-env")
    log.info("Brzy Environment  : {}", env)
    val app = WebApp(env)
    servletContextEvent.getServletContext.setAttribute("application", app)
    app.startup()
  }
  
  def contextDestroyed(servletContextEvent: ServletContextEvent) {
    val app: Any = servletContextEvent.getServletContext.getAttribute("application")
    
    if(app != null)
      app.asInstanceOf[WebApp].shutdown()
  }
}
