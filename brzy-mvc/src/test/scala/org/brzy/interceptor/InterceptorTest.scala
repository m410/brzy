package org.brzy.interceptor

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import util.DynamicVariable
import java.lang.reflect.Method
import javassist.util.proxy.{MethodHandler, ProxyObject, ProxyFactory => PFactory, MethodFilter}
import org.junit.{Before, Ignore, Test}

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
    val targetServiceProxy = factory.create(Array(), Array(), new MyMethodInvoker(new MyManagedFactory))
    assertNotNull(targetServiceProxy)
    assertTrue(targetServiceProxy.isInstanceOf[ProxyObject])
    val target = targetServiceProxy.asInstanceOf[TargetService]
    assertEquals("call: one", target.makeCall("one"))
    assertEquals("call: two", target.makeCall("two"))
    assertNull(MySession.value)
  }

  @Test
  def testNestedCall = {
    val factory1 = new PFactory
    factory1.setSuperclass(classOf[TargetService])
    factory1.setFilter(filter)
    val target = factory1.create(Array(), Array(), new MyMethodInvoker(new MyManagedFactory))

    val factory = new PFactory
    factory.setSuperclass(classOf[ParentTargetService])
    factory.setFilter(filter)
    val classes = Array[Class[_]](target.getClass.getSuperclass)
    val args = Array[AnyRef](target)
    val targetServiceProxy = factory.create(classes, args, new MyMethodInvoker(new MyManagedFactory))
    assertNotNull(targetServiceProxy)
    assertTrue(targetServiceProxy.isInstanceOf[ProxyObject])
    val parent = targetServiceProxy.asInstanceOf[ParentTargetService]
    assertEquals("call: one", parent.nestedCall("one"))
    assertEquals("call: two", parent.nestedCall("two"))
    assertNull(MySession.value)
  }
}

class TargetService {
  def makeCall(arg: String) = {
    println("    target call: " + arg)
    val x ="call: " + arg
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

class MyMethodInvoker[T<:AnyRef](val mf: ManagedFactory[T]) extends MethodHandler {
  override def invoke(self: AnyRef, m1: Method, m2: Method, args: Array[AnyRef]): AnyRef = {
    var returnValue: AnyRef = null
    var nested = false
    val ctx =
        if (mf.context.value == null)
          mf.factory.create
        else {
          nested = true
          mf.context.value
        }

    mf.context.withValue(ctx) {
      returnValue = m2.invoke(self, args: _*)
    }

    if(!nested)
      mf.factory.destroy(ctx)

    returnValue
  }
}


trait Factory[T] {
  def create: T
  def destroy(s: T)
}

trait ManagedFactory[T] {
  val factory:Factory[T]
  val context:DynamicVariable[T]
}

class MySession(txt:String) {
  override def toString = "MySession: " + txt
}

object MySession extends DynamicVariable[MySession](null)

class MyFactory extends Factory[MySession] {
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

class MyManagedFactory extends ManagedFactory[MySession] {
  val context = MySession
  val factory = new MyFactory
}
