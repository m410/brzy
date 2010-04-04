package org.brzy.trx

import org.junit.Assert._
import java.lang.String
import org.junit.{Ignore, Test}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ProxyBuilderTest {

  @Test
  @Ignore
  def testProxy = {
    val proxy = ProxyBuilder.proxy(classOf[ProxyFixture],new ScalaHandler, Array[AnyRef]())
    assertNotNull(proxy)
    assertEquals("hello: Fred", proxy.hello("Fred"))
    println( proxy.hello("Fred"))
  }

//  @Test
//  def testProxyPojo = {
//    val proxy = ProxyBuilder.proxy(classOf[ProxyFixture],new JavaMethodHandler, Array[AnyRef]())
//    assertNotNull(proxy)
//    assertEquals("hello: Fred", proxy.hello("Fred"))
//    println( proxy.hello("Bob"))
//  }
}

class ProxyFixture {
  val nameField = "fieldName"
  def hello(name:String) = "hello: " + name
}

