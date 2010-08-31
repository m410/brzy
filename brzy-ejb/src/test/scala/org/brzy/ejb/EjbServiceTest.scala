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
package org.brzy.ejb

import mock.MyBeanServiceImpl
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.springframework.mock.jndi.SimpleNamingContextBuilder

class EjbServiceTest extends JUnitSuite {
  val builder = new SimpleNamingContextBuilder
  builder.bind("java:/comp/env/MyBeanService", new MyBeanServiceImpl)
  builder.activate

  @Test def testLookup = {
    val config = new EjbModConfig(Map[String,AnyRef](
      "name" -> "EJB",
      "beans" -> List(
          Map(
            "service_name" -> "myBeanService",
            "remote_interface" -> "org.brzy.ejb.mock.MyBeanService",
            "jndi_name" -> "java:/comp/env/MyBeanService")
        )
      ))
    val provider = new EjbModProvider(config)
    assertNotNull(provider.serviceMap)
    assertEquals(1, provider.serviceMap.size)
  }
}

