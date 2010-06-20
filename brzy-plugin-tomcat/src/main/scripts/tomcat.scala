import org.apache.catalina.LifecycleException
import org.apache.catalina.loader.WebappLoader
import org.apache.catalina.realm.MemoryRealm
import org.apache.catalina.startup.Embedded
import java.net.InetAddress
import java.io.File
import collection.JavaConversions._
import org.brzy.tomcat.{ScalaCompiler, FileWatcher}


object Tomcat extends Application {

  val sourceDir = new File(args(0),"src/main/scala")
  val classesDir = new File(args(0),"target/scala_2.8.0.RC5/classes")

  val sb = new StringBuilder
  val libDir = new File(args(0),"target/scala_2.8.0.RC5/webapp/WEB-INF/lib")
  libDir.listFiles.foreach(f => {
    sb.append(f.getAbsolutePath)
    sb.append(":")
  })

  val classpath = sb.toString
  println(" -- source   : " + sourceDir)
  println(" -- classes  : " + classesDir)
  println(" -- classpath: " + classpath )
  new RunWebApp("",8080)
  new FileWatcher(sourceDir,new ScalaCompiler(sourceDir,classesDir,classpath))
  Thread.sleep(100000000) // TODO there's probably a better way to do this
}

class RunWebApp(contextName:String, port:Int) {
	println("Running Web Application")

  val appBase = "scala_2.8.0.RC5/webapp"
  val classesDir = appBase + "/WEB-INF/classes"
	val container = new Embedded

	val catalinaHome = new File("project/brzy-plugins/brzy-tomcat")
	container.setCatalinaHome(catalinaHome.getAbsolutePath)
	// container.setRealm(new MemoryRealm())
  val loader = new WebappLoader(this.getClass.getClassLoader)

  if (classesDir != null)
      loader.addRepository(new File(classesDir).toURI.toURL.toString)

	val targetDir = new File("target")
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

// Start it
Tomcat.main(args)

