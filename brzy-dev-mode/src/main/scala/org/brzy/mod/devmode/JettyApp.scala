package org.brzy.mod.devmode

import java.io.File
import org.brzy.webapp.BrzyFilter
import org.brzy.application.WebAppListener
import org.fusesource.scalate.servlet.TemplateEngineServlet
import org.eclipse.jetty.server.nio.SelectChannelConnector
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.server.session.{HashSessionManager, SessionHandler}
import org.eclipse.jetty.servlet.{ServletHandler, FilterHolder, ServletHolder}
import org.eclipse.jetty.server.handler.ErrorHandler
import org.eclipse.jetty.security.{ConstraintSecurityHandler, SecurityHandler}
import org.eclipse.jetty.server.{SessionIdManager, SessionManager, Connector, Server}

/**
 * Sample jetty app
 *
 * @author Michael Fortin
 */
object JettyApp extends scala.Application {

  val sourceDir = "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-src/"
  val classesDir = "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-classes/"
  val webDir = "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/app-web/"
  val extraPath = "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/src/test/resources/"

  val compilerPath = List(
    classesDir,
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/aspectjweaver-1.6.8.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/beanwrap-0.2.2.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/brzy-scalate-1.0.0.beta3.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/brzy-webapp-1.0.0.beta3.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-fileupload-1.2.2.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-io-1.4.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/commons-logging-1.1.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/dom4j-1.6.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/fab-configuration-0.8.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/google-collections-1.0.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/gson-1.4.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/guava-r09.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/ivy-2.2.0.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/javassist-3.11.0.GA.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/javax.servlet-2.5.0.v201103041518.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/jetty-all-7.6.2.v20120308.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/json-1.1.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/logback-classic-0.9.27.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/logback-core-0.9.27.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/paranamer-2.3.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/reflections-0.9.5-RC2.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scala-compiler-2.8.2.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scala-library-2.8.2.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scalabeans_2.8.1-0.2.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scalate-core-1.5.2-scala_2.8.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scalate-util-1.5.2-scala_2.8.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/slf4j-api-1.6.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/snakeyaml-1.7.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validation-api-1.0.0.GA.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validator-0.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/xml-apis-1.0.b2.jar"
  )

  val server = new Server();

  val connector = new SelectChannelConnector()
  connector.setPort(Integer.getInteger("jetty.port", 8080).intValue())
  server.setConnectors(Array(connector))

  val webapp = new WebAppContext()
  webapp.setContextPath("/")
  webapp.setResourceBase(webDir)
  webapp.setDescriptor("/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/web.xml")
  webapp.setSessionHandler(new SessionHandler(new HashSessionManager()))
//  webapp.setInitParameter("brzy-env", "development")
//
//  webapp.addEventListener(new ApplicationLoadingListener())
//
//  val brzyFil = new FilterHolder(new BrzyFilter())
//  webapp.addFilter(brzyFil, "/*", 1)
//
//  val sspServ = new ServletHolder(new TemplateEngineServlet())
//  webapp.addFilter(brzyFil, "*.ssp", 1)
//
//  val brzyServ = new ServletHolder(new BrzyDynamicServlet())
//  brzyServ.setInitParameter("source_dir", sourceDir)
//  brzyServ.setInitParameter("classes_dir", classesDir)
//  brzyServ.setInitParameter("compiler_path", compilerPath.foldLeft("")((r, c) => r + ":" + c))
//  webapp.addServlet(brzyServ, "*.brzy")

  server.setHandler(webapp);
  server.start()
  server.join()
}
