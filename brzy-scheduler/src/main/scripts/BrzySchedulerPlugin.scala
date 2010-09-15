

@Plugin(name="scheduler-plugin", desc="Generate a scheduler Service")
class BrzySchedulerPlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="gen-scheduler-service",desc="Create a scheduler service")
	def createScheduler(args:Array[String]) = {
		context.line.say(Info("Create a scheduler service"))
	}
}