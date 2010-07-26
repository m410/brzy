package org.brzy.mvc.mock

import org.brzy.application.WebApp
import org.brzy.config.webapp.WebAppConfig


class MockWebApp(config:WebAppConfig) extends WebApp(config) {
  override def makeServices = List()
  override def makeControllers = List()
}