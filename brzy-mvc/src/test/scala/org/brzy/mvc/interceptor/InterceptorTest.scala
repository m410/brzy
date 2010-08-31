/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.mvc.interceptor

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

class MyManagedFactory extends ManagedThreadContext {
  type T = MySession
  val context = MySession
  val empty = new MySession("nil")
  var counter = 0
  def destroySession(s: MySession) = {
    println("destroy: " + s)
  }

  def createSession = {
    println("create : ")
    counter = counter + 1
    new MySession("run-" + counter)
  }
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


class My2ManagedFactory extends ManagedThreadContext {
  type T = My2Session
  val context = My2Session
  val empty = new My2Session("nil")
  var counter = 0

  def destroySession(s: My2Session) = {
    println("destroy2: " + s)
  }

  def createSession = {
    println("create2 : ")
    counter = counter + 1
    new My2Session("run2-" + counter)
  }
}




