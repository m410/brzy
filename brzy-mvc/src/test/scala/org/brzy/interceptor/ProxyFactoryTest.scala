package org.brzy.interceptor

import impl.LoggingInterceptor
import org.junit.Test
import org.junit.Assert._
import org.brzy.saved.intercept.{FooBar, Bar}
import org.scalatest.junit.JUnitSuite

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class ProxyFactoryTest extends JUnitSuite {

  @Test
  def testProxyFactory {
    val bar = ProxyFactory.make(classOf[Bar],Array[AnyRef](), new MethodInvoker with LoggingInterceptor)
    assertNotNull(bar)
    assertEquals("hello Mike", bar.asInstanceOf[Bar].hello("Mike"))
    def clazz = bar.getClass.getSuperclass
  }

  @Test
  def testProxyFactory2 {
    val bar = ProxyFactory.make(classOf[Bar], new MethodInvoker with LoggingInterceptor)
    assertNotNull(bar)
    assertEquals("hello Mike", bar.asInstanceOf[Bar].hello("Mike"))
  }

  @Test
  def testProxyFactory3 {
    val bar = ProxyFactory.make(classOf[FooBar],Array[AnyRef](new Bar), new MethodInvoker with LoggingInterceptor)
    assertNotNull(bar)
    assertEquals("hello Mike, from foobar", bar.asInstanceOf[FooBar].hello("Mike"))
  }
}