

@Plugin(name="cascal-plugin", desc="Generate Cascal domain classes")
class DownloadPlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="cascal-domain",desc="Create a Cascal Domain Class")
	def cascalDomain(args:Array[String]) = {
		talk.say(Info("Create a Cascal Domain Class"))
	}
}