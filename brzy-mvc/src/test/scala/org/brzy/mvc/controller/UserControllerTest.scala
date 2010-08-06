package org.brzy.mvc.controller

import org.junit.Test
import org.junit.Assert._


class UserControllerTest {
  @Test def testGet = {
    val controller = new UserController
    assertNotNull(controller)
    assertNotNull(controller.create)
  }
}
