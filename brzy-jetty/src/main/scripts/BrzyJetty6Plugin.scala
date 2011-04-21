

import java.net.InetAddress

import org.mortbay.jetty.Server
import org.mortbay.jetty.handler._
import org.mortbay.jetty.servlet.Context
import org.mortbay.xml.XmlConfiguration

import org.brzy.fab.build.BuildContext
import org.brzy.fab.print.Info
import org.brzy.fab.file.{File,Files}
import org.brzy.fab.compile.ScalaCompiler
import org.brzy.fab.compile.{Compiler=>SCompiler}
import actors.Actor._
import actors.{Exit, TIMEOUT}
import java.io.{PrintWriter, File=>JFile}

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
  }.start

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
	
	protected[this] val server = new Server()
	protected[this] val connector = new SelectChannelConnector()
	connector.setPort(port)
	connector.setHost("127.0.0.1")
	server.addConnector(connector)

	protected[this] val wac = new WebAppContext()
	wac.setContextPath(contextName)
	wac.setWar("webapp")
	server.setHandler(wac)
	server.setStopAtShutdown(true)
	
	def start = {
		println("Starting.....")
		server.start
	}
	
	def stop = {
		println("Stopping.....")
		server.stop
	}
}


class BrzyJetty6Plugin(ctx:BuildContext) {

	def runJetty = {
		ctx.line.say(Info("Run tomcat",true))
	  val sourceDir = File(ctx.sourceDir,"scala")
	  val classesDir = File(ctx.webappDir,"WEB-INF/classes")
	  val libsDir = File(ctx.webappDir,"WEB-INF/lib")

	  ctx.line.say(Info(" -- source   : " + sourceDir))
	  ctx.line.say(Info(" -- classes  : " + classesDir))
	  ctx.line.say(Info(" -- libs     : " + libsDir))

	  try {
			new RunWebApp("",8080)
		  new FileWatcher(sourceDir,classesDir, libsDir, new ScalaCompiler(new PrintWriter(System.out)))
		}
		catch {
			case e:Exception => 
				println(e.getMessage)
				e.printStackTrace
				ctx.line.endWithError(e)
		}
	
	  Thread.sleep(100000000) // TODO there's probably a better way to do this
	}
}
