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
package org.brzy.service

import org.junit.Test
import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import collection.JavaConversions._
import org.brzy.interceptor.{ProxyFactory, Invoker}
import org.brzy.fab.interceptor.ManagedThreadContext

class AssemblyTest extends JUnitSuite {

  @Test def testInjectServiceToController = {
    val invoker: Invoker = new Invoker(List[ManagedThreadContext]())

    val service = ProxyFactory.make(classOf[MockService], invoker)
    assertNotNull(service)

    val ctlr = ProxyFactory.make(classOf[MockController], Array(service),  invoker)
    assertNotNull(ctlr)

    assertEquals("Called",ctlr.asInstanceOf[MockController].callService)
  }
}  

class MockService {
  def doService = "Called"
}

class MockController(val s:MockService) {
  def this() = this(null)
  def callService =  s.doService
}