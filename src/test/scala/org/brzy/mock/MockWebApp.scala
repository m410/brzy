package org.brzy.mock

import org.brzy.application.{WebAppConfiguration, WebApp}
import org.brzy.controller.Controller
import org.brzy.controller.Controller


class MockWebApp(config:WebAppConfiguration) extends WebApp(config) {

  val userService = new UserService

  override val controllers = List(
    new UserController with MockUserStore,
    new UserArgController(userService)
  )

  override val services = List(userService)
}