package org.brzy.mock

import org.brzy.config.plugin.{PluginResource, Plugin}
import org.brzy.config.webapp.WebAppViewResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class MockPluginResource(c:MockPluginConfig) extends WebAppViewResource(c) {
  val fileExtension = ".ssp"
}