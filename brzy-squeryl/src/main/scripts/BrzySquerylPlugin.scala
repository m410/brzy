

@Plugin(name="squeryl-plugin", desc="Generate Squeryl domain classes")
class BrzySquerylPlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="gen-squeryl-domain",desc="Create a Squeryl Domain Class")
	def createSquerylDomain(args:Array[String]) = {
		context.line.say(Info("Create a Squeryl Domain Class"))
		// ask for package name & class name
		// ask if you want to add the controller
		// ask to create unit test
//		val group = new StringTemplateGroup("mygroup", File(".brzy/modules/brzy-squeryl/templates"))
//		val template = group.template("mytemplate")
//		template.setAttribute("name","value")
//		val outputFile = File("")
//		val writer = new BufferedWriter(new FileWriter(outputFile))
//		writer.write(template.toString)
//		writer.close
	}
}