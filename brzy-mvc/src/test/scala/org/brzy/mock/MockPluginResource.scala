package org.brzy.mock

import org.brzy.config.mod.{ModResource, Mod}
import org.brzy.config.webapp.WebAppViewResource

class MockPluginResource(c:MockPluginConfig) extends WebAppViewResource(c) {
  val fileExtension = ".ssp"
}