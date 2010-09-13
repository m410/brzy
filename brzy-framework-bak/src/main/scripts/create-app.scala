
import io.Source
import java.io.{FileWriter, FileInputStream, FileOutputStream, File}
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.VelocityContext

object CreateApp extends Application {

	case class InitWrapper(name:String, version:String, packageSpace:String, artifact:String)

	def initApp():InitWrapper = {
		println("Create Brzy App")
		print("Project Name : ")
		val name = readLine
		print("Version      : ")
		val version = readLine
		print("Group Name   : ")
		val packageSpace = readLine
		print("Artifict Name: ")
		val artifact = readLine

		println("--Application Properties")
		println("  Name      : " + name)
		println("  Version   : " + version)
		println("  Group     : " + packageSpace)
		println("  Artifact  : " + artifact)
		print(  "accept [y|n]: ")
		val accept = readLine

		if(accept == "y" || accept == "Y")
			InitWrapper(name, version,packageSpace,artifact)
		else
			initApp()
	}

	def recursiveCopy(sourceFile:File, dest:File ):Unit  = {

		if(sourceFile.isDirectory) {
			val dir = new File(dest, sourceFile.getName)
			dir.mkdir
			sourceFile.listFiles.foreach(f => recursiveCopy(f, dir))
		}
		else {
			val destFile = new File(dest, sourceFile.getName)
			val source = new FileInputStream(sourceFile).getChannel
			val destination = new FileOutputStream(destFile).getChannel

			try {
			  destination.transferFrom(source, 0, source.size())
			}
			finally {
				if(source != null)
				 source.close

				if(destination != null)
				 destination.close
			}
		}
	}

	val init = initApp()

	// copy template folder structure
	val brzyBuild = new File(args(1),"templates/project/brzy-app")
	val to = new File(args(0),init.artifact)
	to.mkdir
	brzyBuild.listFiles.foreach(f => recursiveCopy(f, to))

	// create yml file
	val brzyApp = new BrzyAppBYml(init.name, init.version, init.packageSpace, init.artifact)
	val brzyAppFile = new File(to,"brzy-webapp.b.yml")
	val appOut = new java.io.BufferedWriter( new java.io.FileWriter(brzyAppFile) )
	appOut.write(brzyApp.content)
	appOut.close

  val DS = System.getProperty("file.separator")
  val packagePath = init.packageSpace.replaceAll("\\.",DS)
  val sourceFolder = new File(to, "src/scala/" + packagePath)
  sourceFolder.mkdirs

  val props = new java.util.Properties
  props.put("resource.loader", "file")
  props.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader")
  props.put("file.resource.loader.path", args(1) + "/templates")
  props.put("file.resource.loader.cache", "true")
  val velocityEngine = new VelocityEngine
  velocityEngine.init(props)
  val context = new VelocityContext
  context.put("packageName", init.packageSpace)

  val template = velocityEngine.getTemplate("project/WebApp.scala.vm")
  val applicationFile = new File(sourceFolder,"Application.scala")
  val writer = new FileWriter(applicationFile)
  template.merge( context, writer )
  writer.close

  val template2 = velocityEngine.getTemplate("project/HomeController.scala.vm")
  val controllerFile = new File(sourceFolder,"HomeController.scala")
  val writer2 = new FileWriter(controllerFile)
  template2.merge( context, writer2 )
  writer2.close
}

class BrzyAppBYml(name:String, version:String, group:String, artifact:String) {

  private val nameHolder = "[name]"
  private val versionHolder = "[version]"
  private val groupHolder = "[group]"
  private val artifactHolder = "[artifact]"

  val content = Source.fromURL(getClass.getClassLoader.getResource("template.brzy-webapp.b.yml"))
    .mkString
    .replace(nameHolder,name)
    .replace(versionHolder,version)
    .replace(groupHolder,group)
    .replace(artifactHolder,artifact)
}

CreateApp.main(args)
