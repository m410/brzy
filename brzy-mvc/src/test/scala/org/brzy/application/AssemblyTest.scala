package org.brzy.application

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.interceptor.impl.LoggingInterceptor
import org.brzy.interceptor.{ProxyFactory, MethodInvoker}
import collection.JavaConversions._

class AssemblyTest extends JUnitSuite {

  @Test
  def testInjectServiceToController = {
    val service = ProxyFactory.make(classOf[MockService], new MethodInvoker with LoggingInterceptor)
    assertNotNull(service)

    val ctlr = ProxyFactory.make(classOf[MockController], Array(service),  new MethodInvoker with LoggingInterceptor)
    assertNotNull(ctlr)

    assertEquals("Called",ctlr.asInstanceOf[MockController].callService)
  }
}  

class MockService {
    def doService = "Called"
  }

  class MockController(val s:MockService) {
    def this() = this(null)
    def callService =  s.doService
  }