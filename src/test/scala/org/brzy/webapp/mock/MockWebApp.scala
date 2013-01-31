package org.brzy.webapp.mock

import org.brzy.application.{WebAppConfiguration, WebApp}
import org.brzy.webapp.controller.Controller


class MockWebApp(config:WebAppConfiguration) extends WebApp(config) {
  /**
   * controllers declared as val's need to be declared lazy.
   */
  def makeServices = List.empty[AnyRef]

  /**
   * document me
   */
  def makeControllers = List.empty[Controller]
}