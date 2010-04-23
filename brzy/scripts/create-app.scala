
import java.io.{FileInputStream,FileOutputStream, File}
import io.Source

object CreateApp extends Application {
    
	case class InitWrapper(name:String,version:String,packageSpace:String, artifact:String)
	
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
	val brzyAppFile = new File(to,"brzy-app.b.yml")
	val appOut = new java.io.BufferedWriter( new java.io.FileWriter(brzyAppFile) )
	appOut.write(brzyApp.content)
	appOut.close
	
	// TODO create source folders
}

class BrzyAppBYml(name:String, version:String, group:String, artifact:String) {

  private val nameHolder = "[name]"
  private val versionHolder = "[version]"
  private val groupHolder = "[group]"
  private val artifactHolder = "[artifact]"

  val content = Source.fromURL(getClass.getClassLoader.getResource("template.brzy-app.b.yml"))
    .mkString
    .replace(nameHolder,name)
    .replace(versionHolder,version)
    .replace(groupHolder,group)
    .replace(artifactHolder,artifact)
}

CreateApp.main(args)
