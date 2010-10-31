package org.brzy.fab.phase

import org.brzy.fab.build.BuildContext
import org.brzy.application.WebAppConf
import org.brzy.fab.print.Debug
import org.brzy.fab.file.File
import java.io.{File => JFile}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class InitPhase(context:BuildContext) {
  def init = {
    context.line.say(Debug("init"))

    try {
      val projectDir = new JFile(System.getProperty("user.dir"))
      val configFile = File(projectDir, "/brzy-webapp.b.yml")

      if (!configFile.exists)
        context.line.endWithError("No Configuration file at: " + configFile.getAbsolutePath)

      context.line.say(Debug("-config: " + configFile.getAbsolutePath))
      val webAppConfig = WebAppConf.buildtime(File(".brzy/modules"), context.environment)
      context.line.say(Debug("-webAppConfig: " + webAppConfig))
      context.properties += "webAppConfig" -> webAppConfig
    }
    catch {
      case e:Exception => context.line.endWithError(e)
    }
  }
}