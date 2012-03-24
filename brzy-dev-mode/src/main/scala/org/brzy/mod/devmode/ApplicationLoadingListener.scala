package org.brzy.mod.devmode

import javax.servlet.{ServletContextEvent, ServletContextListener}
import org.brzy.application.{WebAppConfiguration, WebApp}
import org.slf4j.LoggerFactory
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import java.net.URLClassLoader
import java.io.File


class ApplicationLoadingListener extends ServletContextListener {

  def contextInitialized(ctx: ServletContextEvent) {
    val lc = LoggerFactory.getILoggerFactory

    if (lc.isInstanceOf[LoggerContext])
      StatusPrinter.print(lc.asInstanceOf[LoggerContext])
    else {
      val factory = LoggerFactory.getILoggerFactory
      println("### LoggerFactory is instance of %s".format(factory.getClass.toString))
    }

    val config = WebAppConfiguration.runtime("developement")
    val projectApplicationClass = config.application.get.applicationClass.get

    val classesDir = "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-classes/"
    val cp = Array(new File(classesDir).toURI.toURL)

    val applicationLoader = new URLClassLoader(cp, getClass.getClassLoader)
    val appClass = applicationLoader.loadClass(projectApplicationClass)
    val constructor = appClass.getConstructor(Array(classOf[WebAppConfiguration]): _*)
    val webapp = constructor.newInstance(Array(config): _*)
    ctx.getServletContext.setAttribute("application", webapp)
  }

  def contextDestroyed(servletContextEvent: ServletContextEvent) {
    val app: Any = servletContextEvent.getServletContext.getAttribute("application")

    if (app != null)
      app.asInstanceOf[WebApp].shutdown()
  }
}