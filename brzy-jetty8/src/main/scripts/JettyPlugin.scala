


import org.brzy.fab.file.{File,Files}
import org.brzy.fab.build.Task
import org.brzy.webapp.BrzyFilter
import org.fusesource.scalate.servlet.TemplateEngineServlet
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.server.Server
import java.util.EnumSet
import javax.servlet.DispatcherType


class JettyPlugin(configPort:Int,messagePort:Int) extends Task(configPort,messagePort) {

	def runJetty() {
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
