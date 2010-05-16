package org.brzy.mock

import org.brzy.application.WebApp
import org.brzy.config.WebappConfig

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class MockWebApp(config:WebappConfig) extends WebApp(config) {
  override val services = Array()
  override val controllers = Array()
}