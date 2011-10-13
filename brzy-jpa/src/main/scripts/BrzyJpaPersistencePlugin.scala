
import org.brzy.fab.file.{Files, File}
import org.brzy.mod.jpa.build.PersistenceXml
import org.brzy.application.WebAppConf
import org.brzy.fab.build.Task

class BrzyJpaPersistencePlugin(configPort:Int,messagePort:Int) extends Task(configPort,messagePort) {
  // create a class from template
	def genPersistenceXml() {
		messenger.info("Create a JPA Domain Class")
    val webappConf = WebAppConf.fromJson(configuration.property("config"))
    val outFile = File(configuration.webappDir, "WEB-INF/classes/META-INF/persistence.xml")
    outFile.getParentFile.mkdirs
    val persistenceXml = new PersistenceXml(webappConf)
    persistenceXml.saveToFile(outFile.getAbsolutePath)
	}
}