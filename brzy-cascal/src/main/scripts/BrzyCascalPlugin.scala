

@Plugin(name="cascal-plugin", desc="Generate Cascal domain classes")
class BrzyCascalPlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="gen-cascal-domain",desc="Create a Cascal Domain Class")
	def cascalDomain(args:Array[String]) = {
		context.line.say(Info("Create a Cascal Domain Class"))
	}
}