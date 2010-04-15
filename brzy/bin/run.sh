#!/bin/sh
exec scala-2.8 -deprecation -classpath \
$BRZY_HOME/lib/annotations-api.jar:\
$BRZY_HOME/lib/catalina-ant.jar:\
$BRZY_HOME/lib/catalina-ha.jar:\
$BRZY_HOME/lib/catalina-tribes.jar:\
$BRZY_HOME/lib/catalina.jar:\
$BRZY_HOME/lib/el-api.jar:\
$BRZY_HOME/lib/jasper-el.jar:\
$BRZY_HOME/lib/jasper-jdt.jar:\
$BRZY_HOME/lib/jasper.jar:\
$BRZY_HOME/lib/jsp-api.jar:\
$BRZY_HOME/lib/servlet-api.jar:\
$BRZY_HOME/lib/tomcat-coyote.jar:\
$BRZY_HOME/lib/tomcat-dbcp.jar:\
$BRZY_HOME/lib/tomcat-i18n-es.jar:\
$BRZY_HOME/lib/tomcat-i18n-fr.jar:\
$BRZY_HOME/lib/tomcat-i18n-ja.jar:\
$BRZY_HOME/lib/tomcat-juli.jar:\
$BRZY_HOME/lib/scala-library.jar:\
$BRZY_HOME/lib/scala-compiler.jar\
 "$0" `pwd` $BRZY_HOME 
!#

import org.apache.catalina.LifecycleException
import org.apache.catalina.loader.WebappLoader
import org.apache.catalina.realm.MemoryRealm
import org.apache.catalina.startup.Embedded
import java.net.InetAddress
import java.io.File


object Tomcat extends Application {
	new RunWebApp("",8080)
}

class RunWebApp(contextName:String, port:Int) {
	println("Running Web Application")
	
  val appBase = "war"
  val classesDir = appBase + "/WEB-INF/classes"
	val container = new Embedded

	val catalinaHome = new File(".brzy/plugin-tomcat")
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

	Thread.sleep(100000000)
}

// Start it
Tomcat.main(args)


