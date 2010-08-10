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

