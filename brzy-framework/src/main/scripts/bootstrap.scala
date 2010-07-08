import java.io._
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.VelocityContext
import org.brzy.config.BootConfigBuilder
import org.brzy.config.mod.Mod
import org.brzy.plugin.Downloader._
import org.brzy.config.{Dependency,Repository}
import collection.JavaConversions._
import org.brzy.webapp.{ConfigFactory, PluginFactory}

@deprecated("user boot1 & boot2")
object Bootstrap extends Application {

  // create .brzy folder
  println(" - make project dir")
  val projectDir = new File(args(0), "project")
  if (!projectDir.exists)
    projectDir.mkdirs

  // TODO create build.properties

  // create plugin folder
  println(" - make plugin dir")
  val brzyPlugins = new File(projectDir, "plugins")
  if (!brzyPlugins.exists)
    brzyPlugins.mkdirs

  // create merged config and put it in the project directory
  println(" - make config")
  val bootBuilder = BootConfigBuilder(new File(args(0),"brzy-webapp.b.yml"), "development")
  val configFile = new File(projectDir, "brzy-webapp.b.yml")
  val config = bootBuilder.config

  // download plugins
  println(" - download plugins")
  download(brzyPlugins, config.views.get)
  config.plugins.get.foreach(it => download(brzyPlugins, it))
  config.persistence.get.foreach(it => download(brzyPlugins, it))

  // reload & save configurations
  println(" - reload config")
  // TODO create full config file, needs new classpath
  val viewPlugin = PluginFactory.makePlugin(config.views,brzyPlugins)
  val persistencePlugins = config.persistence.get.map(f=>PluginFactory.makePlugin(f,brzyPlugins))
  val plugins = config.plugins.get.map(f=>PluginFactory.makePlugin(f,brzyPlugins))
  val webappConfig = ConfigFactory.makeWebappConfig(config,viewPlugin,persistencePlugins,plugins)
  bootBuilder.writeMerged(configFile)

  // create sbt build script
  println(" - create build script")
  val props = new java.util.Properties
  props.put("resource.loader", "file")
  props.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader")
  props.put("file.resource.loader.path", args(1) + "/templates")
  props.put("file.resource.loader.cache", "true")
  val velocityEngine = new VelocityEngine
  velocityEngine.init(props)
  val context = new VelocityContext

  val repos:java.util.Collection[Repository] = {config.views.get.repositories.get ++ config.repositories.get ++ config.plugins.get.map(_.repositories.get)}.asInstanceOf[List[Repository]]
  val deps:java.util.Collection[Dependency] = {config.dependencies.get ++ config.plugins.get.map(_.dependencies.get) ++ config.views.get.dependencies.get}.asInstanceOf[List[Dependency]]
  val plugs:java.util.Collection[Mod] = {List[Mod](config.views.get) ++ config.plugins.get ++ config.persistence.get}.asInstanceOf[List[Mod]]
  context.put("repositories", repos)
  context.put("dependencies", deps)
  context.put("plugins", plugs)

  val template = velocityEngine.getTemplate("project/BrzyWebappProject.scala.vm")
  val buildDir = new File(projectDir, "build")
  if (!buildDir.exists)
    buildDir.mkdirs
  val buildFile = new File(buildDir, "BrzyWebappProject.scala")
  val writer = new FileWriter(buildFile)
  template.merge(context, writer)
  writer.close
}

Bootstrap.main(args)