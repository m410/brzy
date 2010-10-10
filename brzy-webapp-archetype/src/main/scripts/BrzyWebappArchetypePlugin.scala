import java.io.{BufferedWriter, FileWriter}
import org.brzy.fab.print.Question
import org.clapper.scalasti.StringTemplateGroup
import org.brzy.fab.file.{Files, File}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.build.BuildContext

class BrzyWebAppArchetypePlugin(ctx:BuildContext) {
  def create(args:Array[String]) = {
    val org = ctx.line.ask(Question("org: "))
    val name = ctx.line.ask(Question("name: "))
    val version = ctx.line.ask(Question("version: "))

    val dest = File(name)
    dest.mkdirs
    val brzyHome = File.sysPath(System.getenv("BRZY_HOME"))
    val sources = Files(brzyHome,"archetypes/brzy-webapp/layout/*")
    sources.foreach(_.copyTo(dest))

    val group = new StringTemplateGroup("brzy", File(brzyHome, "archetypes/brzy-webapp/templates"))
    val homeControllerTemplate = group.template("HomeController-scala")
    homeControllerTemplate.setAttribute("package", org)
    homeControllerTemplate.setAttribute("name", name)
    homeControllerTemplate.setAttribute("version", version)
    val writer = new BufferedWriter(new FileWriter(File(name + "/src/scala/HomeController.scala")))
    writer.write(homeControllerTemplate.toString)
    writer.close

    val webappTemplate = group.template("WebApp-scala")
    webappTemplate.setAttribute("package", org)
    webappTemplate.setAttribute("name", name)
    webappTemplate.setAttribute("version", version)
    val writer2 = new BufferedWriter(new FileWriter(File(name + "/src/scala/Application.scala")))
    writer2.write(webappTemplate.toString)
    writer2.close

    val brzyWebappTemplate = group.template("brzy-webapp-yml")
    brzyWebappTemplate.setAttribute("package", org)
    brzyWebappTemplate.setAttribute("name", name)
    brzyWebappTemplate.setAttribute("version", version)
    val writer3 = new BufferedWriter(new FileWriter(File(name + "/brzy-webapp.b.yml")))
    writer3.write(brzyWebappTemplate.toString)
    writer3.close
  }
}