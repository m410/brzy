import org.brzy.fab.file.{File, Files}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.build.Task
import org.brzy.fab.common.Classpaths
import org.brzy.mod.jetty.{ApplicationLoadingListener, BrzyServlet, BrzyFilter}
import org.fusesource.scalate.servlet.TemplateEngineServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.server.Server
import java.util.EnumSet
import javax.servlet.DispatcherType
import com.twitter.json.Json

class JettyPlugin extends Task {

  def runJetty() {
    val webDir = configuration.webappDir.getAbsolutePath
    val sourceDir = File(configuration.sourceDir, "scala").getAbsolutePath

    val webClasses = File(configuration.webappDir, "WEB-INF/classes")
    val classesDir = File(configuration.targetDir, "webinf/classes")

    if (webClasses.exists()) {
      val classesDirBase = File(configuration.targetDir, "webinf")
      webClasses.copyTo(classesDirBase)

      File(configuration.webappDir, "WEB-INF").trash()
    }

    val cpaths = Classpaths()
    val runPathJars = cpaths.appClasspaths("compile")

    val runPath = runPathJars ++ List(classesDir)
    val compilerPath = runPath.map(_.toURI.toURL.toExternalForm.substring(5)) // peel off the prefix
            .foldLeft("")((r, c) => r + ":" + c)

    val config = Json.parse(configuration.get("config").asInstanceOf[String]).asInstanceOf[Map[String, _]]
    val appMap = config("config").asInstanceOf[Map[String, _]]("application").asInstanceOf[Map[String, _]]
    val loaderClass = appMap("application_class").asInstanceOf[String] + "Loader"

    messenger.debug("loader: " + loaderClass)
    messenger.debug("webDir: " + webDir)
    messenger.debug("classesDir: " + classesDir.getAbsolutePath)
    messenger.debug("sourceDir: " + sourceDir)
    messenger.debug("compilerPath: " + compilerPath)
    messenger.debug("config properties: " + configuration.properties)

    val server = new Server(8080)
    val webapp = new WebAppContext()
    webapp.setResourceBase(webDir)
    webapp.setContextPath("/")
    server.setHandler(webapp)

    webapp.setInitParameter("brzy-env", "development")
    webapp.addEventListener(new ApplicationLoadingListener(loaderClass, runPath.map(_.toURI.toURL).toArray))


    val brzyServ = webapp.addServlet(classOf[BrzyServlet], "*.brzy")
    brzyServ.setInitParameter("source_dir", sourceDir)
    brzyServ.setInitParameter("classes_dir", classesDir.getAbsolutePath)
    brzyServ.setInitParameter("compiler_path", compilerPath)
    brzyServ.setInitParameter("loader_class", loaderClass)
    brzyServ.setInitOrder(1)
    brzyServ.setEnabled(true)

    webapp.addFilter(classOf[BrzyFilter], "/*", EnumSet.of(DispatcherType.REQUEST))

    webapp.addServlet(classOf[TemplateEngineServlet], "*.ssp")

    server.start()
    server.join()
  }
}
