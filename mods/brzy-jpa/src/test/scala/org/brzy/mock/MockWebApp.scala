package org.brzy.mock

import org.brzy.application.WebApp
import org.brzy.config.webapp.WebAppConfig

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class MockWebApp(config:WebAppConfig) extends WebApp(config) {
  override def makeServices = List()
  override def makeControllers = List()
}