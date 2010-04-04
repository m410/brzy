package org.brzy.saved.intercept

import org.junit.Test
import org.junit.Ignore
import org.junit.Assert._
import java.lang.reflect.{Method, InvocationHandler}

/**
 * @author Michael Fortin
 * @version $Id : $
 */
class InterceptorTest {

  @Test
  def testMixin = {
    val component = new ManagedComponentProxy(new FooImpl) with LoggingInterceptor
    assertNotNull(component)
  }

  @Test
  def testInterceptor = {
    var foo = ManagedComponentFactory.createComponent[Foo](
      classOf[Foo],
      new ManagedComponentProxy(new FooImpl) with LoggingInterceptor with TransactionInterceptor
      )
    foo.hello
  }

  @Test
	@Ignore
  def testInterceptorBar = {
    var bar = ManagedComponentFactory.createComponent[Bar](
      classOf[Bar],
      new ManagedComponentProxy(new Bar) with LoggingInterceptor with TransactionInterceptor
      )
    assertEquals("hello Mike",bar.hello("Mike"))
  }
}

//
//class Handler extends InvocationHandler {
//  def invoke(proxy: AnyRef, m: Method, args: Array[AnyRef]): AnyRef = {
//    m.invoke(proxy, args)
//  }
//}

