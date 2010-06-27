package org.brzy.interceptor

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import util.DynamicVariable
import java.lang.reflect.Method
import javassist.util.proxy.{MethodHandler, ProxyObject, ProxyFactory => PFactory, MethodFilter}
import org.junit.Test
import org.apache.commons.lang.builder.{HashCodeBuilder, EqualsBuilder}
import collection.immutable.List

class InterceptorTest extends JUnitSuite {
  val filter = new MethodFilter {
    def isHandled(m: Method) =
      !m.getName.equals("finalize") &&
              !m.getName.equals("toString") &&
              !m.getName.equals("clone") &&
              !m.getName.equals("wait") &&
              !m.getName.equals("notify") &&
              !m.getName.equals("hashCode") &&
              !m.getName.equals("equals")
  }

  @Test
  def testMultipleThreadLocals = {
    val factory = new PFactory
    factory.setSuperclass(classOf[TargetService])
    factory.setFilter(filter)
    val targetServiceProxy = factory.create(Array(), Array(), new Invoker(List(new MyManagedFactory)))
    assertNotNull(targetServiceProxy)
    assertTrue(targetServiceProxy.isInstanceOf[ProxyObject])
    val target = targetServiceProxy.asInstanceOf[TargetService]
    assertEquals("call: one", target.makeCall("one"))
    assertEquals("call: two", target.makeCall("two"))
    assertEquals(new MySession("nil"), MySession.value)
  }

  @Test
  def testNestedCall = {
    val factory1 = new PFactory
    factory1.setSuperclass(classOf[TargetService])
    factory1.setFilter(filter)
    val target = factory1.create(Array(), Array(), new Invoker(List(new MyManagedFactory)))

    val factory = new PFactory
    factory.setSuperclass(classOf[ParentTargetService])
    factory.setFilter(filter)
    val classes = Array[Class[_]](target.getClass.getSuperclass)
    val args = Array[AnyRef](target)
    val targetServiceProxy = factory.create(classes, args, new Invoker(List(new MyManagedFactory)))
    assertNotNull(targetServiceProxy)
    assertTrue(targetServiceProxy.isInstanceOf[ProxyObject])
    val parent = targetServiceProxy.asInstanceOf[ParentTargetService]
    assertEquals("call: one", parent.nestedCall("one"))
    assertEquals("call: two", parent.nestedCall("two"))
    assertEquals(new MySession("nil"), MySession.value)
  }

  @Test
  def testNoFactories = {
    val factory = new PFactory
    factory.setSuperclass(classOf[TargetService])
    factory.setFilter(filter)
    val targetServiceProxy = factory.create(Array(), Array(), new Invoker(List()))
    assertNotNull(targetServiceProxy)
    assertTrue(targetServiceProxy.isInstanceOf[ProxyObject])
    val target = targetServiceProxy.asInstanceOf[TargetService]
    assertEquals("call: one", target.makeCall("one"))
    assertEquals("call: two", target.makeCall("two"))
  }

  @Test
  def testManyFactories = {
    val factory = new PFactory
    factory.setSuperclass(classOf[TargetService])
    factory.setFilter(filter)
    val list:List[ManagedThreadContext] = List(new MyManagedFactory,new My2ManagedFactory)
    val targetServiceProxy = factory.create(Array(), Array(), new Invoker(list))
    assertNotNull(targetServiceProxy)
    assertTrue(targetServiceProxy.isInstanceOf[ProxyObject])
    val target = targetServiceProxy.asInstanceOf[TargetService]
    assertEquals("call: one", target.makeCall("one"))
    assertEquals("call: two", target.makeCall("two"))
    assertEquals(new MySession("nil"), MySession.value)
    assertEquals(new My2Session("nil"), My2Session.value)
  }
}



class TargetService {
  def makeCall(arg: String) = {
    println("    target call: " + arg)
    val x = "call: " + arg
    println(" target session: " + MySession.value)
    x
  }
}

class ParentTargetService(t: TargetService) {
  def nestedCall(s: String) = {
    println("    parent call: " + s)
    val out = t.makeCall(s)
    println(" parent session: " + MySession.value)
    out
  }
}


class MySession(var txt: String) {
  override def toString = "MySession: " + txt

  override def equals(p1: Any) = {
    val that = p1.asInstanceOf[MySession]
    new EqualsBuilder()
            .append(this.txt, that.txt)
            .isEquals()
  }

  override def hashCode = {
    new HashCodeBuilder(1, 3)
            .append(this.txt)
            .toHashCode
  }
}

object MySession extends DynamicVariable[MySession](new MySession("nil"))

class MyFactory extends ContextFactory[MySession] {
  var counter = 0

  def destroy(s: MySession) = {
    println("destroy: " + s)
  }

  def create = {
    println("create : ")
    counter = counter + 1
    new MySession("run-" + counter)
  }
}

class MyManagedFactory extends ManagedThreadContext {
  type T = MySession
  val context = MySession
  val factory = new MyFactory
  val matcher = new MethodMatcher {
    def isMatch(a: AnyRef, m: Method) = true
  }
  val emptyState = new MySession("nil")
}



class My2Session(var txt: String) {
  override def toString = "My2Session: " + txt

  override def equals(p1: Any) = {
    val that = p1.asInstanceOf[My2Session]
    new EqualsBuilder()
            .append(this.txt, that.txt)
            .isEquals()
  }

  override def hashCode = {
    new HashCodeBuilder(1, 3)
            .append(this.txt)
            .toHashCode
  }
}

object My2Session extends DynamicVariable[My2Session](new My2Session("nil"))

class My2Factory extends ContextFactory[My2Session] {
  var counter = 0

  def destroy(s: My2Session) = {
    println("destroy2: " + s)
  }

  def create = {
    println("create2 : ")
    counter = counter + 1
    new My2Session("run2-" + counter)
  }
}

class My2ManagedFactory extends ManagedThreadContext {
  type T = My2Session
  val context = My2Session
  val factory = new My2Factory
  val matcher = new MethodMatcher {
    def isMatch(a: AnyRef, m: Method) = true
  }
  val emptyState = new My2Session("nil")
}




