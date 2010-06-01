package org.brzy.mock

import org.brzy.application.WebApp
import org.brzy.config.BootConfig

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class MockWebApp(config:BootConfig) extends WebApp(config) {
  override val services = Array()
  override val controllers = Array()
}