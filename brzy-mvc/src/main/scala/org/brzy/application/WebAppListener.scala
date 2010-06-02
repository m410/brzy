package org.brzy.application

import javax.servlet.{ServletContextEvent, ServletContextListener}
import org.slf4j.LoggerFactory
import org.brzy.webapp.{WebAppConfig, ConfigFactory}
import java.io.File

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebAppListener extends ServletContextListener {

  private val log = LoggerFactory.getLogger(classOf[WebAppListener])

  def contextInitialized(servletContextEvent: ServletContextEvent) = {
    val env = servletContextEvent.getServletContext.getInitParameter("brzy-env")
    log.info("Brzy Environment  : {}", env)
    val url = getClass.getClassLoader.getResource("brzy-webapp.b.yml")
    log.info("Brzy Configuration: {}", url)
    val app = WebAppFactory.create(url, env)
    servletContextEvent.getServletContext.setAttribute("application", app)
    app.startup
  }
  
  def contextDestroyed(servletContextEvent: ServletContextEvent) = {
    val app: Any = servletContextEvent.getServletContext.getAttribute("application")
    
    if(app != null)
      app.asInstanceOf[WebApp].shutdown
  }
}
