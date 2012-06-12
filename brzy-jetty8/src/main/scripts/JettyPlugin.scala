import ch.qos.logback.classic.joran.JoranConfigurator
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.core.util.StatusPrinter
import java.io.{FileWriter, BufferedWriter}
import org.brzy.fab.file.{File, Files}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.build.Task
import org.brzy.fab.common.Classpaths
import org.brzy.mod.jetty.{ScalateWrapperServlet, BrzyServlet, BrzyFilter}
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.server.Server
import java.util.EnumSet
import javax.servlet.DispatcherType
import com.twitter.json.Json
import org.slf4j.LoggerFactory

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
    val runPathPre = runPathJars ++ List(classesDir)
    val runPath = runPathPre.map(_.toURI.toURL.toExternalForm.substring(5)) // strip prefix
            .foldLeft("")((r, c) => r + ":" + c)

    val compilerPathJars = cpaths.appClasspaths("compile") ++ cpaths.appClasspaths("provided") ++ List(classesDir)
    val compilerPath = compilerPathJars.map(_.toURI.toURL.toExternalForm.substring(5)) // stip prefix
            .foldLeft("")((r, c) => r + ":" + c)

    val config = Json.parse(configuration.get("config").asInstanceOf[String]).asInstanceOf[Map[String, _]]
    val appMap = config("config").asInstanceOf[Map[String, _]]("application").asInstanceOf[Map[String, _]]
    val loaderClass = appMap("application_class").asInstanceOf[String] + "Loader"

    messenger.debug("loader: " + loaderClass)
    messenger.debug("webDir: " + webDir)
    messenger.debug("classesDir: " + classesDir.getAbsolutePath)
    messenger.debug("sourceDir: " + sourceDir)
    messenger.debug("runPath: " + runPath)
    messenger.debug("compilerPath: " + compilerPath)
    messenger.debug("config properties: " + configuration.properties)

    val loggerContext:LoggerContext  = LoggerFactory.getILoggerFactory.asInstanceOf[LoggerContext]
    val configurator:JoranConfigurator  = new JoranConfigurator()
    configurator.setContext(loggerContext)
    configurator.doConfigure(makeLogConfig())
    StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext)

    val server = new Server(8080)
    val webapp = new WebAppContext()
    webapp.setResourceBase(webDir)
    webapp.setContextPath("/")
    server.setHandler(webapp)

    webapp.setInitParameter("brzy-env", "development")

    val filter = webapp.addFilter(classOf[BrzyFilter], "/*", EnumSet.of(DispatcherType.REQUEST))
    filter.setInitParameter("source_dir", sourceDir)
    filter.setInitParameter("classes_dir", classesDir.getAbsolutePath)
    filter.setInitParameter("compiler_path", compilerPath)
    filter.setInitParameter("run_path", runPath)
    filter.setInitParameter("loader_class", loaderClass)

    webapp.addServlet(classOf[BrzyServlet], "*.brzy")

    webapp.addServlet(classOf[ScalateWrapperServlet], "*.ssp")

    server.start()
    server.join()
  }

  def makeLogConfig() = {
    val file = File(configuration.targetDir, "logback-server-confg.xml")
    val writer = new BufferedWriter(new FileWriter(file))
    writer.write("""<configuration>
                   |  <appender name="FILE" class="ch.qos.logback.core.FileAppender">
                   |    <file>jetty.log</file>
                   |    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
                   |      <Pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</Pattern>
                   |    </encoder>
                   |  </appender>
                   |  <logger name="org.eclipse" level="error"><appender-ref ref="FILE"/></logger>
                   |  <logger name="org.hibernate" level="error"><appender-ref ref="FILE"/></logger>
                   |  <root level="info">
                   |    <appender-ref ref="FILE" />
                   |  </root>
                   |</configuration>""".stripMargin)
    writer.close()
    file
  }
}
