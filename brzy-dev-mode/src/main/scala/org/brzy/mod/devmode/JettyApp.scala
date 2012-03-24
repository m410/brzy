package org.brzy.mod.devmode

import org.mortbay.jetty.webapp.WebAppContext
import java.io.File
import org.brzy.webapp.BrzyFilter
import org.brzy.application.WebAppListener
import org.fusesource.scalate.servlet.TemplateEngineServlet
import org.mortbay.jetty.{SessionManager, Server}
import org.mortbay.jetty.servlet._

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
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/brzy-webapp-1.0.0.beta3.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/brzy-scalate-1.0.0.beta3.jar",
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
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/jetty-6.0.2.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/jetty-util-6.0.2.jar",
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
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/scalatest_2.8.1-1.7.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/servlet-api-2.5-6.0.2.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/slf4j-api-1.6.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/snakeyaml-1.7.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validation-api-1.0.0.GA.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/validator-0.1.jar",
    "/Users/m410/Projects/Brzy/brzy-webapp/brzy-dev-mode/target/dependency/xml-apis-1.0.b2.jar"
  )

  val server = new Server(8080)
  val warUrlString = new File(webDir).toURI.toURL.toExternalForm
  val context = new WebAppContext(warUrlString, "/")
//  val context = new Context(server, "/", Context.SESSIONS)
//  context.setResourceBase(warUrlString)

  val map = new java.util.HashMap[String, String]()
  map.put("brzy-env","development")
  context.setInitParams(map)
//  context.setExtraClasspath(extraPath)

//  context.setSessionHandler(new SessionHandler(new HashSessionManager()))
  context.addEventListener(new ApplicationLoadingListener())

  val brzyFil = new FilterHolder(new BrzyFilter())
  context.addFilter(brzyFil,"/*",1)

  val sspServ = new ServletHolder(new TemplateEngineServlet())
  context.addFilter(brzyFil,"*.ssp",1)

  val brzyServ = new ServletHolder(new BrzyDynamicServlet())
  brzyServ.setInitParameter("source_dir",sourceDir)
  brzyServ.setInitParameter("classes_dir",classesDir)
  brzyServ.setInitParameter("compiler_path",compilerPath.foldLeft("")((r,c) => r+":"+c))
  context.addServlet(brzyServ, "*.brzy")

  server.setHandler(context)
  server.start()
  server.join()
}
