#!/bin/sh
exec scala "$0" `pwd` $BRZY_HOME 
!#

import java.io.File

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
	// if(!brzyConfig.exists)
	// 	throw new RuntimeException("This is not a brzy application directory, run 'brzy create-app' first")
	
	// dowload default jars
	// ant, ivy, scala-compiler, scala-library	
	// 					val ivy = Source.fromUrl("")
	// val url = new java.net.URL("http://www.treas.gov/offices/enforcement/ofac/sdn/delimit/sdn.csv")
	// 	val urlConnection = url.openConnection
	// 	urlConnection.connect()
	// 	InputStream input = url.openStream
	//  input.close()
	// 		
	// create default build scripts
	val brzyBuild = new File(args(1),"templates/project/build.xml")	
	val to = new File(args(0),".brzy/build.xml")	
	val out = new java.io.BufferedWriter( new java.io.FileWriter(to) );
  io.Source.fromFile(brzyBuild).getLines.foreach(s => out.write(s,0,s.length));
  out.close()

	// generate build.properties
	// generate ivy.xml
	// generate ivysettings.xml
	
	// download plugins
	
}
Initialize.main(args)
