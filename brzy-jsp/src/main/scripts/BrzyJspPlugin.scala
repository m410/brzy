import java.io.{BufferedWriter, FileWriter}
import org.brzy.fab.print.{Question,Info}
import org.clapper.scalasti.StringTemplateGroup
import org.brzy.fab.file.{Files, File}
import org.brzy.fab.file.FileUtils._
import org.brzy.fab.build.BuildContext

class BrzyJspPlugin(context:BuildContext) {

	def genJspViews(args:Array[String]) = {
		context.line.say(Info("Generate Views"))
	}
}