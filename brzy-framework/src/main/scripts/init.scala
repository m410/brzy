import java.io._
import org.brzy.build._
import org.brzy.config.{AppConfig,Builder}
import scala.io.Source

object Initialize extends Application {
  println(" Initializing project")

	// create .brzy folder
	val brzyDir = new File(args(0), ".brzy")
	if(!brzyDir.exists)
		brzyDir.mkdirs
		
	// create plugin folder
	val brzyPlugins = new File(brzyDir,"plugins")
	if(!brzyPlugins.exists)
		brzyPlugins.mkdirs
	
	// load configuration
	val brzyConfig = new File(args(0),"brzy-app.b.yml")
	val config = new Builder(brzyConfig,"development").runtimeConfig // dev is placeholder
		
	// create default build scripts
	val brzyBuild = new File(args(1),"templates/build/brzy-app/build.xml")	
	val to = new File(brzyDir,"build.xml")	
	val source = new FileInputStream(brzyBuild).getChannel
	val destination = new FileOutputStream(to).getChannel
 	destination.transferFrom(source, 0, source.size())
	source.close
	destination.close

	val brzyCom = new File(args(1),"templates/build/brzy-app/common.xml")	
	val to2 = new File(brzyDir,"common.xml")	
	val source2 = new FileInputStream(brzyCom).getChannel
	val destination2 = new FileOutputStream(to2).getChannel
 	destination2.transferFrom(source2, 0, source2.size())
	source2.close
	destination2.close

	// generate build.properties
	val buildProperties = new BuildProperties(config)
	val bpFile = new File(brzyDir,"build.properties")
	val bpOut = new BufferedWriter(new FileWriter(bpFile))
	bpOut.write(buildProperties.content)
	bpOut.close
	
	// generate ivy.xml
	val ivyXml = new IvyXml(config)
	val ixFile = new File(brzyDir,"ivy.xml")
	val ixOut = new BufferedWriter(new FileWriter(ixFile))
	ixOut.write(ivyXml.body.toString)
	ixOut.close
	
	// generate ivysettings.xml
	val ivysettingsXml = new IvySettingsXml(config)
	val isxFile = new File(brzyDir,"ivysettings.xml")
	val isxOut = new BufferedWriter(new FileWriter(isxFile))
	isxOut.write(ivysettingsXml.body.toString)
	isxOut.close
	
	// TODO write timestamp
}
Initialize.main(args)
