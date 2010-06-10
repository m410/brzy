import java.io._
import org.brzy.webapp.ConfigFactory._

object Boot1 extends Application {

  // create .brzy folder
  println(" - make project dir")
  val projectDir = new File(args(0), "project")
  if (!projectDir.exists)
    projectDir.mkdirs

  // TODO create build.properties

  // create plugin folder
  println(" - make plugin dir")
  val brzyPlugins = new File(projectDir, "brzy-plugins")
  if (!brzyPlugins.exists)
    brzyPlugins.mkdirs

  // create merged config and put it in the project directory
  println(" - make config")
  val config = makeBootConfig(new File(args(0),"brzy-webapp.b.yml"), "development")
  val configFile = new File(projectDir, "brzy-webapp.development.b.yml")
  writeConfigToFile(config,configFile)

  // download plugins
  println(" - download plugins")
  installPlugin(brzyPlugins, config.views.get)
  config.plugins.get.foreach(it => installPlugin(brzyPlugins, it))
  config.persistence.get.foreach(it => installPlugin(brzyPlugins, it))
}

Boot1.main(args)