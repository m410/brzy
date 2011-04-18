import org.brzy.fab.build.Task

class BrzySecurityPlugin extends Task {

	def genLogin(args:Array[String]) = {
		messenger.info("Generate a login")
	}

	def genRegistration(args:Array[String]) = {
		messenger.info("Generate a registration page")
	}
}