package org.brzy.interceptor

import org.junit.Test
import org.junit.Assert._
import org.brzy.saved.intercept.{FooBar, Bar}
import org.scalatest.junit.JUnitSuite
import util.DynamicVariable
import java.lang.reflect.Method
import collection.immutable.List


class ProxyFactoryTest extends JUnitSuite {
  @Test def testProxyFactory = {
    val list: List[FixtureManagedThreadContext] = List(new FixtureManagedThreadContext)
    val bar = ProxyFactory.make(classOf[Bar], Array[AnyRef](), new Invoker(list))
    assertNotNull(bar)
    assertEquals("hello Mike", bar.asInstanceOf[Bar].hello("Mike"))
    def clazz = bar.getClass.getSuperclass
  }

  @Test def testProxyFactory2 = {
    val list: List[FixtureManagedThreadContext] = List(new FixtureManagedThreadContext)
    val bar = ProxyFactory.make(classOf[Bar], new Invoker(list))
    assertNotNull(bar)
    assertEquals("hello Mike", bar.asInstanceOf[Bar].hello("Mike"))
  }

  @Test def testProxyFactory3 = {
    val list: List[FixtureManagedThreadContext] = List(new FixtureManagedThreadContext)
    val bar = ProxyFactory.make(classOf[FooBar], Array[AnyRef](new Bar), new Invoker(list))
    assertNotNull(bar)
    assertEquals("hello Mike, from foobar", bar.asInstanceOf[FooBar].hello("Mike"))
  }
}

class FixtureSession(var t: String)

object FixtureSession extends DynamicVariable(new FixtureSession(""))

class FixtureManagedThreadContext extends ManagedThreadContext {
  type T = FixtureSession

  val empty = new FixtureSession("")

  val matcher = new MethodMatcher {
    def isMatch(a: AnyRef, m: Method) = true
  }

  val context = FixtureSession

  val factory = new ContextFactory[FixtureSession] {
    private var count = 0

    def destroy(s: FixtureSession) = {}

    def create = {
      count = count + 1
      new FixtureSession("fs-" + count)
    }
  }
}