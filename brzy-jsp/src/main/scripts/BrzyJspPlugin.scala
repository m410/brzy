

@Plugin(name="jsp-plugin", desc="Generate JSP view pages")
class BrzyJspPlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="gen-jsp-views",desc="Create jsp crud pages for a domain class")
	def genJspViews(args:Array[String]) = {
		context.line.say(Info("Generate Views"))
	}
}