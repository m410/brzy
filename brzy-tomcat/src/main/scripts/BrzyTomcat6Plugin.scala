

import java.net.InetAddress

import org.apache.catalina.LifecycleException
import org.apache.catalina.loader.WebappLoader
import org.apache.catalina.startup.Embedded

import org.brzy.fab.file.{File,Files}
import org.brzy.fab.compile.ScalaCompiler
import org.brzy.fab.compile.{Compiler=>SCompiler}
import actors.Actor._
import actors.{Exit, TIMEOUT}
import java.io.{PrintWriter, File=>JFile}
import org.brzy.fab.build.Task

/*
 * watch the source directory for changes
 */
class FileWatcher(baseDir: JFile, destDir: JFile, libDir: JFile, compiler: SCompiler) {
  val paths = findFiles(baseDir)
  val libs = Files(libDir, "*.jar").toArray
  private[this] var lastModified: Long = System.currentTimeMillis

  private[this] val watcher = actor {
    loop {
      reactWithin(100) {
        case TIMEOUT =>
          paths.find(_.lastModified > lastModified) match {
            case Some(f) =>
              lastModified = System.currentTimeMillis
              val success = compiler.compile(baseDir, destDir, libs)
              f
            case _ => // nothing
          }
        case Exit => exit()
      }
    }
  }.start()

  private def findFiles(root: JFile): List[JFile] = {
    if (root.isFile && root.getName.endsWith(".scala"))
      List(root)
    else
      makeList(root.listFiles).flatMap {f => findFiles(f)}
  }

  private def makeList(a: Array[JFile]): List[JFile] = {
    if (a == null)
      Nil
    else
      a.toList
  }
}

/*
  Runs tomcat
 */
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
  engine.setDefaultHost(host.getName)
  container.addEngine(engine)

  val httpConnector = container.createConnector(null.asInstanceOf[InetAddress], port, false)
  container.addConnector(httpConnector)

  container.setAwait(true)
  container.start()

	Runtime.getRuntime.addShutdownHook(new Thread() {
		override def run() {
			try {
				if (container != null)
					container.stop()
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


class BrzyTomcat6Plugin extends Task {

	def runTomcat() {
		messenger.info("Run tomcat")
	  val sourceDir = File(configuration.sourceDir,"scala")
	  val classesDir = File(configuration.webappDir,"WEB-INF/classes")
	  val libsDir = File(configuration.webappDir,"WEB-INF/lib")

	  messenger.info(" -- source   : " + sourceDir)
	  messenger.info(" -- classes  : " + classesDir)
	  messenger.info(" -- libs     : " + libsDir)

	  try {
			new RunWebApp("",8080)
		  new FileWatcher(sourceDir,classesDir, libsDir, new ScalaCompiler(new PrintWriter(System.out)))
		}
		catch {
			case e:Exception => messenger.warn(e.getMessage,e)
		}
	
	  Thread.sleep(100000000) // TODO there's probably a better way to do this
	}
}
