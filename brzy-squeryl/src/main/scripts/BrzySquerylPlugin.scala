
import java.io.{BufferedWriter, FileWriter}
import org.clapper.scalasti.StringTemplateGroup
import org.brzy.fab.file.{Files, File}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.build.Task

class BrzySquerylPlugin extends Task  {

	def createSquerylDomain(args:Array[String]) {
		messenger.info("Create a Squeryl Domain Class")

		val packageAndClass =
      if(args.length == 1)
        args(0)
      else
        messenger.ask("enter package & class: ")

		val className = packageAndClass.substring(packageAndClass.lastIndexOf(".") +1)
    val packageName = packageAndClass.substring(0,packageAndClass.lastIndexOf("."))
		// ask if you want to add the controller
		// ask to create unit test

		val group = new StringTemplateGroup("mygroup", File(".fab/modules/brzy-squeryl/templates"))
		val template = group.template("domain-scala")
		template.setAttribute("packageName",packageName)
		template.setAttribute("className",className)

    val outputDir = File("src/scala" + packageName.split("\\.").foldLeft("")((r,c)=> r + "/" + c))
    outputDir.mkdirs
    val outputFile = File(outputDir, className + ".scala")
    
		val writer = new BufferedWriter(new FileWriter(outputFile))
		writer.write(template.toString)
		writer.close
	}

  def createAuthDomain(args:Array[String]) {
    messenger.info("Create a Squeryl Domain Class")

    val packageAndClass =
      if(args.length == 1)
        args(0)
      else
        messenger.ask("enter package for Person and Authority: ")

    val group = new StringTemplateGroup("mygroup", File(".fab/modules/brzy-squeryl/templates"))
    val outputDir = File("src/scala" + packageName.split("\\.").foldLeft("")((r,c)=> r + "/" + c))
    outputDir.mkdirs

    val userTemplate = group.template("domain-user-scala")
    userTemplate.setAttribute("packageName",packageName)
    userTemplate.setAttribute("className",className)
    userTemplate.setAttribute("attributeName", attributeName(className))
    val userFile = File(outputDir, "Person.scala")
    val userWriter = new BufferedWriter(new FileWriter(userFile))
    userWriter.write(userTemplate.toString)
    userWriter.close()

    val roleTemplate = group.template("domain-auth-scala")
    roleTemplate.setAttribute("packageName",packageName)
    roleTemplate.setAttribute("className",className)
    roleTemplate.setAttribute("attributeName", attributeName(className))
    val roleFile = File(outputDir, "Authority.scala")
    val roleWriter = new BufferedWriter(new FileWriter(roleFile))
    roleWriter.write(roleTemplate.toString)
    roleWriter.close()
  }
}