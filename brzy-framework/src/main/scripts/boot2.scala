import java.io._
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.VelocityContext
import org.brzy.config.common.{Dependency, Repository}
import org.brzy.config.plugin.Plugin
import collection.JavaConversions._
import org.brzy.webapp.ConfigFactory._

/**
 * called after boot1, this loads with a classpath that includes the plugins that boot1 downloaded
 */
object Boot2 extends Application {

  val projectDir = new File(args(0), "project")
  val brzyPlugins = new File(projectDir, "brzy-plugins")

  val bootConfig = makeBootConfig(new File(args(0),"brzy-webapp.b.yml"), "development")
  val configFile = new File(projectDir, "brzy-webapp.development.b.yml")
  val viewPlugin = makeBuildTimePlugin(bootConfig.views.get,brzyPlugins)
  val persistence = bootConfig.persistence.get.map(makeBuildTimePlugin(_,brzyPlugins))
  val plugins = bootConfig.plugins.get.map(makeBuildTimePlugin(_,brzyPlugins))
  val config = makeWebAppConfig(bootConfig,viewPlugin, persistence, plugins)

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

  val repos:java.util.Collection[Repository] = {
    config.views.repositories ++ config.repositories.toList ++ config.plugins.map(_.repositories)
  }.asInstanceOf[List[Repository]]

  val deps:java.util.Collection[Dependency] = {
    config.dependencies.toList ++ config.plugins.map(_.dependencies) ++ config.views.dependencies
  }.asInstanceOf[List[Dependency]]

  val plugs:java.util.Collection[Plugin] = {
    List(config.views) ++ config.plugins ++ config.persistence
  }.asInstanceOf[List[Plugin]]

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

Boot2.main(args)