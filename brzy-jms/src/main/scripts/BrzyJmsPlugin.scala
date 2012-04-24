
import java.io.{BufferedWriter, FileWriter}
import org.clapper.scalasti.StringTemplateGroup
import org.brzy.fab.file.{Files, File}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.build.Task

class BrzyJmsPlugin extends Task  {

	def genJsmService(args:Array[String]) {
		messenger.info("Create a JMS Service Class")

    val packageAndClass =
      if(args.length == 1)
        args(0)
      else
         messenger.ask("enter package & class: ")

		val className = packageAndClass.substring(packageAndClass.lastIndexOf(".") +1)
    val packageName = packageAndClass.substring(0,packageAndClass.lastIndexOf("."))
		// ask if you want to add the controller
		// ask to create unit test

		val group = new StringTemplateGroup("mygroup", File(".fab/modules/brzy-jms/templates"))
		val template = group.template("service-scala")
		template.setAttribute("packageName",packageName)
		template.setAttribute("className",className)

    val outputDir = File("src/scala" + packageName.split("\\.").foldLeft("")((r,c)=> r + "/" + c))
    outputDir.mkdirs
    val outputFile = File(outputDir, className + ".scala")    

		val writer = new BufferedWriter(new FileWriter(outputFile))
		writer.write(template.toString)
		writer.close
	}
}