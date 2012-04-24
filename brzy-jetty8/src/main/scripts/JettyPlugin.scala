


import org.brzy.fab.file.{File,Files}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.build.Task
import org.fusesource.scalate.servlet.TemplateEngineServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.server.Server
import java.util.EnumSet
import javax.servlet.DispatcherType
import org.brzy.mod.jetty.{BrzyServlet, BrzyFilter}

class JettyPlugin extends Task {

  def runJetty() {
    val webDir = configuration.webappDir.getAbsolutePath
    val sourceDir = File(configuration.sourceDir,"scala").getAbsolutePath
    val classesDir = File(configuration.webappDir,"WEB-INF/classes").getAbsolutePath
    val files = Files(".fab/fab/brzy-jetty8/*.jar") ++ Files(".fab/app/compile/*.jar")
    val compilerPath = {files ++ List(File(classesDir))}
            .map(_.toURI.toURL.toExternalForm.substring(5))
            .foldLeft("")((r, c) => r + ":" + c)

    val loaderClass = "org.brzy.jpajsp.ApplicationLoader"
    // TODO need to add the configuration the classes dir, with configuration files

    messenger.debug("webDir: " + webDir)
    messenger.debug("classesDir: " + classesDir)
    messenger.debug("sourceDir: " + sourceDir)
    messenger.debug("compilerPath: " + compilerPath)
    messenger.debug("config properties: " + configuration.properties)
    messenger.debug("config modules: " + configuration.get("modules").asInstanceOf[String])

    val server = new Server(8080)
    val webapp = new WebAppContext()
    webapp.setResourceBase(webDir)
    webapp.setContextPath("/")
    server.setHandler(webapp)

    webapp.setInitParameter("brzy-env", "development")
//    webapp.addEventListener(new ApplicationLoadingListener())

    val brzyServ = webapp.addServlet(classOf[BrzyServlet], "*.brzy")
    brzyServ.setInitParameter("source_dir", sourceDir)
    brzyServ.setInitParameter("classes_dir", classesDir)
    brzyServ.setInitParameter("compiler_path", compilerPath)
    brzyServ.setInitParameter("loader_class", loaderClass)

    webapp.addServlet(classOf[TemplateEngineServlet], "*.ssp")

    webapp.addFilter(classOf[BrzyFilter],"/*",EnumSet.of(DispatcherType.REQUEST))

    server.start()
    server.join()
  }
}
