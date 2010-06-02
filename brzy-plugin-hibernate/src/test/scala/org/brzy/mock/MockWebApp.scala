package org.brzy.mock

import org.brzy.application.WebApp
import org.brzy.config.BootConfig
import org.brzy.webapp.WebAppConfig

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class MockWebApp(config:WebAppConfig) extends WebApp(config) {
  override val services = Array()
  override val controllers = Array()
}