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
package org.brzy.jms

import mock.MockJmsService
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.apache.activemq.ActiveMQConnectionFactory

class JmsServiceTest extends JUnitSuite {
  val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")

  @Test def testJms = {
    val jms:JmsService = new JmsService(connectionFactory)
    jms.send("Test Message","test.queue")

    val config = new JmsModConfig(Map(
      "name" -> "JMS Test",
      "scan_package" -> "org.brzy.jms.mock",
      "broker_url" -> "vm://localhost?broker.persistent=false"
    ))
    val provider = new JmsModProvider(config)
    assertNotNull(provider.serviceMap)
    assertEquals(1,provider.serviceMap.size)

    val service = provider.serviceMap("mockJmsService")
    provider.startup
    assertNotNull(service)
//    assertTrue(service.asInstanceOf[MockJmsService].didGetIt)
    provider.shutdown
  }
}

