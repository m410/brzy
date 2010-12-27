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
package org.brzy.interceptor

import org.junit.Test
import org.junit.Assert._
import org.brzy.webapp.mock.{FooBar, Bar}
import org.scalatest.junit.JUnitSuite
import util.DynamicVariable
import java.lang.reflect.Method
import collection.immutable.List
import org.brzy.fab.interceptor.ManagedThreadContext


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
  var count = 0
  val empty = new FixtureSession("")

  val matcher = new MethodMatcher {
    def isMatch(a: AnyRef, m: Method) = true
  }

  val context = FixtureSession

  def destroySession(s: FixtureSession) = {}

  def createSession = {
    count = count + 1
    new FixtureSession("fs-" + count)
  }
}