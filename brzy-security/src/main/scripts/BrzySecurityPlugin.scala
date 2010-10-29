

class BrzySecurityPlugin(context:BuildContext) {

	def genLogin(args:Array[String]) = {
		talk.say(Info("Generate a login"))
	}

	def genRegistration(args:Array[String]) = {
		talk.say(Info("Generate a registration page"))
	}
}