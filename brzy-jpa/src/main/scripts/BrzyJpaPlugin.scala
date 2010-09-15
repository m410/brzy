

@Plugin(name="jpa-plugin", desc="Generate JPA Domain classes")
class BrzyJpaPlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="gen-jpa-domain",desc="Create a JPA Domain Class")
	def genJpaDomain(args:Array[String]) = {
		context.line.say(Info("Create a Cascal Domain Class"))
	}
}