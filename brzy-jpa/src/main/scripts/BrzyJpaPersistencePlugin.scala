
import org.brzy.fab.print.{Question,Info}
import org.brzy.fab.file.{Files, File}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.build.BuildContext

class BrzyJpaPersistencePlugin(context:BuildContext)  {

  // create a class from template
	def genPersistenceXml(args:Array[String]) = {
		context.line.say(Info("Create a JPA Domain Class"))
    
	}
}