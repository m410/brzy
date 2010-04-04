package org.brzy.interceptor

import impl.LoggingInterceptor
import org.junit.Test
import org.junit.Assert._
import org.brzy.saved.intercept.{FooBar, Bar}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ProxyFactoryTest {

  @Test
  def testProxyFactory {
    val bar = ProxyFactory.make(classOf[Bar],Array[AnyRef](), new Proxy with LoggingInterceptor)
    assertNotNull(bar)
    assertEquals("hello Mike", bar.hello("Mike"))
    def clazz = bar.getClass.getSuperclass
  }

  @Test
  def testProxyFactory2 {
    val bar = ProxyFactory.make(classOf[Bar], new Proxy with LoggingInterceptor)
    assertNotNull(bar)
    assertEquals("hello Mike", bar.hello("Mike"))
  }

  @Test
  def testProxyFactory3 {
    val bar = ProxyFactory.make(classOf[FooBar],Array[AnyRef](new Bar), new Proxy with LoggingInterceptor)
    assertNotNull(bar)
    assertEquals("hello Mike, from foobar", bar.hello("Mike"))
  }
}