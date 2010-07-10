import java.io._
import org.brzy.shell.BuildProperties
import org.brzy.webapp.ConfigFactory._

object Boot1 extends Application {

  // create .brzy folder
  println(" - make project dir")
  val projectDir = new File(args(0), "project")
  if (!projectDir.exists)
    projectDir.mkdirs

  // create modules folder
  println(" - make modules dir")
  val brzyMods = new File(projectDir, "brzy-modules")
  if (!brzyMods.exists)
    brzyMods.mkdirs

  // create merged config and put it in the project directory
  println(" - make config")
  val config = makeBootConfig(new File(args(0),"brzy-webapp.b.yml"), "development")
  val configFile = new File(projectDir, "brzy-webapp.development.b.yml")
  writeConfigToFile(config,configFile)

  // make sbt build.properties file
  val buildProperties = new BuildProperties(config)
	val bpFile = new File(projectDir,"build.properties")
	val bpOut = new BufferedWriter(new FileWriter(bpFile))
	bpOut.write(buildProperties.content)
	bpOut.close

  // download modules
  println(" - download modules")
  installModule(brzyMods, config.views.get)

  if(config.modules.isDefined)
    config.modules.get.foreach(it => installPlugin(brzyMods, it))

  if(config.persistence.isDefined)
    config.persistence.get.foreach(it => installPlugin(brzyMods, it))
}

Boot1.main(args)