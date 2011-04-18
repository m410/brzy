import java.io.{BufferedWriter, FileWriter}
import org.clapper.scalasti.StringTemplateGroup
import org.brzy.fab.file.{Files, File}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.reflect.Properties
import org.brzy.fab.build.Task

class BrzyScalatePlugin extends Task  {

	def genSspViews(args:Array[String]) {
		messenger.info("Generate views for domain")

    val packageAndClass =
      if(args.length == 1)
        args(0)
      else
        messenger.ask("enter package & class: ")

		val className = packageAndClass.substring(packageAndClass.lastIndexOf(".") +1)
    val packageName = packageAndClass.substring(0,packageAndClass.lastIndexOf("."))
    val name = className.substring(0,1).toLowerCase + className.substring(1)

    val fields =
      try {
        Properties.listForName(packageAndClass)
      }
      catch {
        case unknown =>
          messenger.warn("No class found by name: " + packageAndClass)
          Array.empty[String]
      }

		val templateDir = File(".brzy/modules/brzy-scalate/templates")
    val group = new StringTemplateGroup("brzy", templateDir)
    val outdir = File("webapp/" + name)
    outdir.mkdirs

    val viewTemplate = group.template("view-ssp")
    viewTemplate.setAttribute("shortClassName", className)
    viewTemplate.setAttribute("attributeName", name)
    viewTemplate.setAttribute("fullClassName", packageAndClass)
    viewTemplate.setAttribute("fields", fields)
    val viewFile = File(outdir, "view.ssp")
    val writer1 = new BufferedWriter(new FileWriter(viewFile))
    writer1.write(viewTemplate.toString)
    writer1.close

    val createTemplate = group.template("create-ssp")
    createTemplate.setAttribute("shortClassName", className)
    createTemplate.setAttribute("attributeName", name)
    createTemplate.setAttribute("fullClassName", packageAndClass)
    createTemplate.setAttribute("fields", fields)
    val createFile = File(outdir, "create.ssp")
    val writer2 = new BufferedWriter(new FileWriter(createFile))
    writer2.write(createTemplate.toString)
    writer2.close

    val editTemplate = group.template("edit-ssp")
    editTemplate.setAttribute("shortClassName", className)
    editTemplate.setAttribute("attributeName", name)
    editTemplate.setAttribute("fullClassName", packageAndClass)
    editTemplate.setAttribute("fields", fields)
    val editFile = File(outdir, "edit.ssp")
    val writer3 = new BufferedWriter(new FileWriter(editFile))
    writer3.write(editTemplate.toString)
    writer3.close

    val listTemplate = group.template("list-ssp")
    listTemplate.setAttribute("shortClassName", className)
    listTemplate.setAttribute("fullClassName", packageAndClass)
    listTemplate.setAttribute("attributeName", name)
    listTemplate.setAttribute("fields", fields)
    listTemplate.setAttribute("fieldsLength", fields.length)
    val listFile = File(outdir, "list.ssp")
    val writer4 = new BufferedWriter(new FileWriter(listFile))
    writer4.write(listTemplate.toString)
    writer4.close
	}
}