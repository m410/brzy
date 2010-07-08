package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.interceptor.{Invoker, ProxyFactory}
import org.squeryl.Session

class SquerylThreadContextTest extends JUnitSuite {
  @Test def testContext = {
    val ctx = new SquerylContextManager("org.h2.Driver", "jdbc:h2:squery-test", "sa", "")
    val ctlr = ProxyFactory.make(classOf[MockController], new Invoker(List(ctx)))
    assertNotNull(ctlr)
    assertEquals("hello Mike", ctlr.asInstanceOf[MockController].doit("Mike"))
//    assertEquals(None,Session.currentSession) // throws Runtime exception as expected
  }
}

class MockController {
  def doit(str: String) = {
    assertNotNull(Session.currentSession)
    "hello " + str
  }
}

