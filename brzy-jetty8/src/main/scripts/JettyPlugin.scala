


import org.brzy.fab.file.{File,Files}
import org.brzy.fab.build.Task
import org.brzy.webapp.BrzyFilter
import org.fusesource.scalate.servlet.TemplateEngineServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.server.Server
import java.util.EnumSet
import javax.servlet.DispatcherType
import org.brzy.mod.jetty.{BrzyDynamicServlet, ApplicationLoadingListener}

class JettyPlugin(configPort:Int,messagePort:Int) extends Task(configPort,messagePort) {

  def runJetty() {
    val webDir = configuration.webappDir.getAbsolutePath
    val sourceDir = configuration.sourceDir.getAbsolutePath
    val classesDir = File(configuration.targetDir,"classes").getAbsolutePath
    val compilerPath = {Files(configuration.cacheDir,"fab/jetty-8/*") ++ List(File(classesDir))}.map(_.getAbsolutePath)

    messenger.debug("webDir: " + webDir)
    messenger.debug("classesDir: " + classesDir)
    messenger.debug("sourceDir: " + sourceDir)
    messenger.debug("compilerPath: " + compilerPath)

    val server = new Server(8080)
    val webapp = new WebAppContext()
    webapp.setResourceBase(webDir)
    webapp.setContextPath("/")
    server.setHandler(webapp)

    webapp.setInitParameter("brzy-env", "development")
    webapp.addEventListener(new ApplicationLoadingListener())

    webapp.addFilter(classOf[BrzyFilter],"/*",EnumSet.of(DispatcherType.REQUEST))
    webapp.addServlet(classOf[TemplateEngineServlet], "*.ssp")

    val brzyServ = webapp.addServlet(classOf[BrzyDynamicServlet], "*.brzy")
    brzyServ.setInitParameter("source_dir", sourceDir)
    brzyServ.setInitParameter("classes_dir", classesDir)
    brzyServ.setInitParameter("compiler_path", compilerPath.foldLeft("")((r, c) => r + ":" + c))

    server.start()
    server.join()
  }
}
