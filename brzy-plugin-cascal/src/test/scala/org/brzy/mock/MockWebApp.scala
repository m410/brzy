package org.brzy.mock

import org.brzy.application.WebApp
import org.brzy.config.AppConfig

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class MockWebApp(config:AppConfig) extends WebApp(config) {
  override val services = Array()
  override val controllers = Array()
}