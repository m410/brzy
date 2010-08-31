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
package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.mvc.interceptor.{Invoker, ProxyFactory}
import org.squeryl.Session

class SquerylThreadContextTest extends JUnitSuite {
  @Test def testContext = {
    val ctx = new SquerylContextManager("org.h2.Driver", "jdbc:h2:squery-test", "sa", "")
    val ctlr = ProxyFactory.make(classOf[MockController], new Invoker(List(ctx)))
    assertNotNull(ctlr)
    assertEquals("hello Mike", ctlr.asInstanceOf[MockController].doit("Mike"))
//    assertEquals(None,Session.currentSession) // throws Runtime exception as expected
  }
}

class MockController {
  def doit(str: String) = {
    assertNotNull(Session.currentSession)
    "hello " + str
  }
}

