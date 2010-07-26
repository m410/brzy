package org.brzy.mvc.mock

import org.brzy.config.mod.{ModResource, Mod}
import org.brzy.config.webapp.WebAppViewResource

class MockModResource(c:MockModConfig) extends WebAppViewResource(c) {
  val fileExtension = ".ssp"
  override val name = c.name.get
}