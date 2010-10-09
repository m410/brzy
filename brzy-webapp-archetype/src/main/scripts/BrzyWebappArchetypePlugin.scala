import java.io.{BufferedWriter, FileWriter}
import org.clapper.scalasti.StringTemplateGroup
import org.brzy.fab.file.{Files, File}
import org.brzy.fab.build.BuildContext
import java.io.{File=>JFile}

class BrzyWebAppArchetypePlugin(ctx:BuildContext) {
  def create(args:Array[String]) = {
    val version = ""
    val name = ""
    val org = ""

    val dest = File(name)
    dest.mkdirs
    val brzyHome = new JFile(System.getenv("BRZY_HOME"))
    val sources = Files(brzyHome,"archetypes/brzy-webapp/project/*")
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