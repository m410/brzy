package org.brzy.mock

import org.brzy.config.webapp.WebAppViewResource

class MockModResource(c:MockModConfig) extends WebAppViewResource(c) {
  val fileExtension = ".ssp"
}