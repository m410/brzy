
import org.brzy.fab.print.{Question,Info}
import org.brzy.fab.file.{Files, File}

import org.brzy.fab.build.BuildContext
import org.brzy.mod.jpa.build.PersistenceXml
import org.brzy.application.WebAppConf

class BrzyJpaPersistencePlugin(context:BuildContext)  {

  // create a class from template
	def genPersistenceXml = {
		context.line.say(Info("Create a JPA Domain Class",true))
    val webappConf = context.properties("webAppConfig").asInstanceOf[WebAppConf]
    val outFile = File(context.webappDir, "WEB-INF/classes/META-INF/persistence.xml")
    outFile.getParentFile.mkdirs
    val persistenceXml = new PersistenceXml(webappConf)
    persistenceXml.saveToFile(outFile.getAbsolutePath)
	}
}