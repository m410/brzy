import java.io._
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.VelocityContext
import org.brzy.config.BootConfigBuilder
import org.brzy.plugin.Plugin
import org.brzy.config.{Dependency,Repository}
import collection.JavaConversions._

object Bootstrap extends Application {
  // create .brzy folder
  val projectDir = new File(args(0), "project")
  if (!projectDir.exists)
    projectDir.mkdirs
  println("make project")

  // create plugin folder
  val brzyPlugins = new File(projectDir, "plugins")
  if (!brzyPlugins.exists)
    brzyPlugins.mkdirs
  println("make plugins")

  // create merged config and put it in the project directory
  val bootBuilder = BootConfigBuilder(new File(args(0),"brzy-webapp.b.yml"), "development")
  val configFile = new File(projectDir, "brzy-webapp.b.yml")
  val config = bootBuilder.config
  bootBuilder.writeMerged(configFile)
  println("make boot config")

  // create sbt build script
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
  val plugs:java.util.Collection[Plugin] = {List[Plugin](config.views.get) ++ config.plugins.get ++ config.persistence.get}.asInstanceOf[List[Plugin]]
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