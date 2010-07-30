package org.brzy.security.mock

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite

class PersonControllerTest extends JUnitSuite {

  @Test def testGet = {
    val controller = new PersonController
    assertNotNull(controller)
//    val setup = manager.factory.create
//    val keyId: String = "myKey"
//
//    manager.context.withValue(setup) {
//
//    }
//    manager.factory.destroy(setup)

  }
}