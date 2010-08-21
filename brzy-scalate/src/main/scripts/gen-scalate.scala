import java.io.{FileWriter, File}
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.VelocityContext
import org.brzy.config.common.{Dependency, Repository}
import org.brzy.config.mod.Mod
import collection.JavaConversions._
import org.brzy.webapp.ConfigFactory._

GenScalate.main(args)

object GenScalate extends Application {
  println(" - GenScalate " + args.toList)

  // setup the configuration
  val projectDir = new File(args(0), "project")
  val brzyMods = new File(projectDir, "brzy-modules")

  val bootConfig = makeBootConfig(new File(args(0),"brzy-webapp.b.yml"), "development")
  val viewModule = makeBuildTimeModule(bootConfig.views.get,brzyMods)
  val persistence = bootConfig.persistence.get.map(makeBuildTimeModule(_,brzyMods))
  val modules =
    if(bootConfig.modules.isDefined)
      bootConfig.modules.get.map(makeBuildTimeModule(_,brzyMods))
    else
      Nil
  val config = makeWebAppConfig(bootConfig,viewModule, persistence, modules)

  // make the template
  val props = new java.util.Properties
  props.put("resource.loader", "file")
  props.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader")
  props.put("file.resource.loader.path", args(1) + "/templates")
  props.put("file.resource.loader.cache", "true")
  val velocityEngine = new VelocityEngine
  velocityEngine.init(props)
  val context = new VelocityContext

  // package
  // classnme
  // class properties

  // setup the context
  context.put("packageName", "org.brzy.testmake")
  context.put("className", "TestGenerate")
  context.put("classProperties", "TestGenerate")

  // write the file
  val template = velocityEngine.getTemplate("create.ssp.vm")
  val buildDir = new File(projectDir, "build")
  if (!buildDir.exists)
    buildDir.mkdirs
  val buildFile = new File(buildDir, "BrzyWebappProject.scala")
  val writer = new FileWriter(buildFile)
  template.merge(context, writer)
  writer.close
}
