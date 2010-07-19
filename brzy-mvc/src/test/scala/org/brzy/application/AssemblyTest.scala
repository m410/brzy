package org.brzy.application

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import collection.JavaConversions._
import org.brzy.interceptor.{ManagedThreadContext, ProxyFactory, Invoker}

class AssemblyTest extends JUnitSuite {

  @Test
  def testInjectServiceToController = {
    val invoker: Invoker = new Invoker(List[ManagedThreadContext]())

    val service = ProxyFactory.make(classOf[MockService], invoker)
    assertNotNull(service)

    val ctlr = ProxyFactory.make(classOf[MockController], Array(service),  invoker)
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