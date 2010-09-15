

@Plugin(name="tomcat-plugin", desc="Run a tomcat development server")
class BrzyTomcat6Plugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="tomcat6",desc="Run tomcat")
	def runTomcat(args:Array[String]) = {
		context.line.say(Info("Run tomcat"))
	}
}