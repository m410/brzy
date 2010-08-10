package org.brzy.ejb

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import javax.naming.{InitialContext, Context}

class EjbServiceTest extends JUnitSuite {
  System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory")
  System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming")
  
  val ic = new InitialContext()
  ic.createSubcontext("java:")
  ic.createSubcontext("java:/comp")
  ic.createSubcontext("java:/comp/env")
  ic.bind("java:/comp/env/MyBean", new MyBean)

  @Test def testLookup = {
    val config = new EjbModConfig(Map[String,AnyRef]())
    val provider = new EjbModProvider(config)
    assertNotNull(provider.serviceMap)
    assertEquals(1, provider.serviceMap.size)
  }
}

class MyBean {
  def sayHello = "Hello"
}