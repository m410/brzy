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

import javax.servlet.{DispatcherType, ServletContextEvent, ServletContextListener}
import org.slf4j.LoggerFactory
import ch.qos.logback.core.util.StatusPrinter
import ch.qos.logback.classic.LoggerContext
import java.util

/**
 * Web application listener placed in the web.xml to initialize and destroy the 
 * application.  This Loads the Webapp, and places it in the application context.
 *
 * @see http://www.softwareengineeringsolutions.com/blogs/2010/08/01/programmatic-definition-of-components-in-servlet-specification-3-0/
 * @author Michael Fortin
 */
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

    val servletContext = servletContextEvent.getServletContext

    val env = servletContext.getInitParameter("brzy-env")
    log.info("Brzy Environment  : {}", env)
    val app = WebApp(env)
    servletContext.setAttribute("application", app)
    app.startup()

    val main = servletContext.addServlet("BrzyServlet", "org.brzy.BrzyServlet")
    main.addMapping("*.brzy")

    val async = servletContext.addServlet("BrzyAsyncServlet", "org.brzy.BrzyAsuncServlet")
    async.setAsyncSupported(true)
    async.addMapping("*.brzy_async")

    val filter = servletContext.addFilter("BrzyFilter", "org.brzy.BrzyFilter")
    val dispatchTypes = util.EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD)
    filter.addMappingForUrlPatterns(dispatchTypes,true,"/*")

    // todo this really should be added dynamically
    val scalate = servletContext.addServlet("ScalateServlet", "org.fusesource.scalate.servlet.TemplateEngineServlet")
    scalate.addMapping("*.ssp")
    scalate.addMapping("*.jade")
    scalate.addMapping("*.scaml")
  }
  
  def contextDestroyed(servletContextEvent: ServletContextEvent) {
    val app: Any = servletContextEvent.getServletContext.getAttribute("application")
    
    if(app != null)
      app.asInstanceOf[WebApp].shutdown()
  }
}
