package org.brzy.mock

import org.brzy.config.webapp.WebAppViewResource
import org.brzy.mock.MockPluginConfig

class MockPluginResource(c:MockPluginConfig) extends WebAppViewResource(c) {
  val fileExtension = ".ssp"
}