

@Plugin(name="scalate-plugin", desc="Generate SSP view pages")
class BrzyScalatePlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="gen-ssp-views",desc="Create CRUD views with Scalate")
	def genSspViews(args:Array[String]) = {
		context.line.say(Info("Generate views for domain"))
	}
}