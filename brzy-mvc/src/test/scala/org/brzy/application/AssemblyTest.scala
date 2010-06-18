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

//    println("-------- service proxy methods ------------")
//    service.getClass.getMethods.foreach(println(_))

    val ctlr = ProxyFactory.make(classOf[MockController], Array(service),  new MethodInvoker with LoggingInterceptor)
    assertNotNull(ctlr)

//    println("-------- controller super methods ------------")
//    ctlr.getClass.getSuperclass.getMethods.foreach(println(_))
//    println("-------- controller proxy methods ------------")
//    ctlr.getClass.getMethods.foreach(println(_))

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