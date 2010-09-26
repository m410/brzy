

import java.net.InetAddress

import org.apache.catalina.LifecycleException
import org.apache.catalina.loader.WebappLoader
import org.apache.catalina.startup.Embedded

import org.brzy.fab.file.{File,Files}

@Plugin(name="tomcat-plugin", desc="Run a tomcat development server")
class BrzyTomcat6Plugin(ctx:BuildContext) extends BuildPlugin(ctx) {

	@Task(name="tomcat6",desc="Run tomcat")
	def runTomcat() = {
		ctx.line.say(Info("Run tomcat",true))
	  val sourceDir = File(ctx.sourceDir,"scala")
	  val classesDir = File(ctx.webappDir,"WEB-INF/classes")
	  val libs = Files(ctx.webappDir,"WEB-INF/lib/*.jar")

	  ctx.line.say(Info(" -- source   : " + sourceDir))
	  ctx.line.say(Info(" -- classes  : " + classesDir))
	  ctx.line.say(Info(" -- classpath: " + libs.mkString(", ") ))
	  // TODO copy files to web-inf
	  new RunWebApp("",8080)
	//  new FileWatcher(sourceDir,new ScalaCompiler(sourceDir,classesDir,classpath))
	  Thread.sleep(100000000) // TODO there's probably a better way to do this
	}
}

class RunWebApp(contextName:String, port:Int) {
	println("Running Web Application")
  val appBase = "webapp"
	val container = new Embedded
	val catalinaHome = File(".brzy/modules/brzy-tomcat")
	container.setCatalinaHome(catalinaHome.getAbsolutePath)
  val loader = new WebappLoader(this.getClass.getClassLoader)
	val targetDir = File("")
	val targetPath = targetDir.getAbsolutePath
  val host = container.createHost("localhost", targetPath)

	val context = container.createContext("/" + contextName, appBase)
  context.setLoader(loader)
  context.setReloadable(true)
  host.addChild(context)

  val engine = container.createEngine()
  engine.setName("engine")
  engine.addChild(host)
  engine.setDefaultHost(host.getName())
  container.addEngine(engine)

  val httpConnector = container.createConnector(null.asInstanceOf[InetAddress], port, false)
  container.addConnector(httpConnector)

  container.setAwait(true)
  container.start

	Runtime.getRuntime.addShutdownHook(new Thread() {
		override def run = {
			try {
				if (container != null)
					container.stop
				println("Shutdown...")
        // TODO delete web-inf
			}
			catch {
				case e:LifecycleException =>
					println("exception: " + e)
				case _ =>
					println("Unknown Exception" )
			}
		}
	})
}
