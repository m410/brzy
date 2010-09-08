import java.io._
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.VelocityContext
import org.brzy.config.common.{Dependency, Repository}
import org.brzy.config.mod.Mod
import collection.JavaConversions._
import org.brzy.config.ConfigFactory._

/**
 * called after boot1, this loads with a classpath that includes the modules that boot1 downloaded
 */
object Boot2 extends Application {

  val projectDir = new File(args(0), "project")
  val brzyMods = new File(projectDir, "brzy-modules")

  val bootConfig = makeBootConfig(new File(args(0),"brzy-webapp.b.yml"), "development")
  val configFile = new File(projectDir, "brzy-webapp.development.b.yml")
  val viewModule = makeBuildTimeModule(bootConfig.views.get,brzyMods)
  val persistence = bootConfig.persistence.get.map(makeBuildTimeModule(_,brzyMods))
  val modules =
    if(bootConfig.modules.isDefined)
      bootConfig.modules.get.map(makeBuildTimeModule(_,brzyMods))
    else
      Nil
  val config = makeWebAppConfig(bootConfig,viewModule, persistence, modules)

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
    config.views.repositories ++ config.repositories.toList ++ config.modules.map(_.repositories)
  }.asInstanceOf[List[Repository]]

  val deps:java.util.Collection[Dependency] = {
    config.dependencies.toList ++ config.modules.map(_.dependencies) ++ config.views.dependencies
  }.asInstanceOf[List[Dependency]]

  val mods:java.util.Collection[Mod] = {
    List(config.views) ++ config.modules ++ config.persistence
  }.asInstanceOf[List[Mod]]

  context.put("repositories", repos)
  context.put("dependencies", deps)
  context.put("modules", mods)

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