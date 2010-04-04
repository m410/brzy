package org.brzy.application

import javax.servlet.{ServletContextEvent, ServletContextListener}
import org.slf4j.LoggerFactory
import org.brzy.migration.Migrator
import org.brzy.config.Builder
import java.net.URL

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class WebAppListener extends ServletContextListener {

  private val log = LoggerFactory.getLogger(classOf[WebAppListener])

  def contextInitialized(servletContextEvent: ServletContextEvent) = {
    val env = servletContextEvent.getServletContext.getInitParameter("brzy-env")
    log.info("Brzy Environment  : {}", env)
    val url = getClass.getClassLoader.getResource("brzy-app.b.yml")
    log.info("Brzy Configuration: {}", url)
    val app = new Builder(url,env).config.initApp
    servletContextEvent.getServletContext.setAttribute("application", app)

    if(app.config.db_migration)
      Migrator.doMigrations

    app.startup
  }
  
  def contextDestroyed(servletContextEvent: ServletContextEvent) = {
    servletContextEvent.getServletContext.getAttribute("application").asInstanceOf[WebApp].shutdown
  }

}