import java.io.{BufferedWriter, FileWriter}
import org.clapper.scalasti.StringTemplateGroup
import org.brzy.fab.build.BuildContext

class BrzyScalatePlugin(context:BuildContext)  {

	def genSspViews(args:Array[String]) = {
		context.line.say(Info("Generate views for domain"))

    // todo check for class name arg, if not ask for it
    // load it, get the properties

    val group = new StringTemplateGroup("brzy", File(brzyHome, ".brzy/modules/brzy-scalate/templates"))


    val viewTemplate = group.template("view-ssp")
    // todo set attributes
    viewTemplate.setAttribute("name", name)
    val viewFile: Any = File(name + "/webapp/" ++ "/HomeController.scala")
    // todo check if it exists, and ask to overwrite
    val writer = new BufferedWriter(new FileWriter(viewFile))
    writer.write(viewTemplate.toString)
    writer.close
	}
}