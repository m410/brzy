package org.brzy.mock

import org.brzy.config.plugin.{PluginResource, Plugin}
import org.brzy.config.webapp.WebAppViewResource

class MockPluginResource(c:MockPluginConfig) extends WebAppViewResource(c) {
  val fileExtension = ".ssp"
}