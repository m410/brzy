#!/bin/sh
exec scala-2.8 -cp $BRZY_HOME/lib/brzy-all-0.1.jar:$BRZY_HOME/lib/jyaml-1.3.jar "$0" `pwd` $BRZY_HOME 
!#

import java.io._
import org.brzy.build._
import org.brzy.config.{Config,Builder}
import scala.io.Source

object Initialize extends Application {
  println(" Initializing project")

	// create .brzy folder
	val brzyDir = new File(args(0), ".brzy")
	if(!brzyDir.exists)
		brzyDir.mkdirs
		
	// create lib folder
	val brzyLib = new File(brzyDir,"lib")
	if(!brzyLib.exists)
		brzyLib.mkdirs
		
	// create plugin folder
	val brzyPlugins = new File(brzyDir,"plugins")
	if(!brzyPlugins.exists)
		brzyPlugins.mkdirs
	
	// load configuration
	val brzyConfig = new File(args(0),"brzy-app.b.yml")
	val config = new Builder(brzyConfig,"development").config // dev is placeholder
		
	// create default build scripts
	val brzyBuild = new File(args(1),"templates/project/build.xml")	
	val to = new File(brzyDir,"build.xml")	
	val source = new FileInputStream(brzyBuild).getChannel
	val destination = new FileOutputStream(to).getChannel
 	destination.transferFrom(source, 0, source.size())
	source.close
	destination.close


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
	
	// write timestamp
	
	// copy scala jar files
	
}
Initialize.main(args)
