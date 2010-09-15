

@Plugin(name="security-plugin", desc="Generate security resources")
class BrzySecurityPlugin(context:BuildContext) extends BuildPlugin(context) {

	@Task(name="gen-login",desc="Create Login")
	def genLogin(args:Array[String]) = {
		talk.say(Info("Generate a login"))
	}

	@Task(name="gen-registration",desc="Create a registration page")
	def genRegistration(args:Array[String]) = {
		talk.say(Info("Generate a registration page"))
	}
}