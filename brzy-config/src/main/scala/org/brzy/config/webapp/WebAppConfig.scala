package org.brzy.webapp

import org.brzy.config.plugin.Plugin
import org.brzy.config._

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */

class WebAppConfig(init: BootConfig,
        val views: Plugin,
        val persistence: List[Plugin],
        val plugins: List[Plugin]) {

  val environment: String = init.environment.get
  val application: Application = init.application.get
  val project: Project = init.project.get
  val logging: Logging = init.logging.get

}