

@Plugin(name="jms-plugin", desc="Generate Jms Client Services")
class BrzyJmsPlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="gen-jms-service",desc="Create a client Service")
	def genJsmService(args:Array[String]) = {
		context.line.say(Info("Create a JMS Service Class"))
	}
}