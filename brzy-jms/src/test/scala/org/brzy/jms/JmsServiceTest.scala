package org.brzy.jms

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.apache.activemq.ActiveMQConnectionFactory

class JmsServiceTest extends JUnitSuite {
  val connectionFactory = new ActiveMQConnectionFactory("vm://localhost?broker.persistent=false")

  @Test def testJms = {
    val service = new JmsService(connectionFactory)
    service.send("Test Message","test.queue")

    // TODO create resource to search and create services
    assertTrue(false)
  }
}

@Queue(name="test.queue")
class TestJmsService {
  onMessage(msg:String) ={
    println("msg: "  + msg)
  }
}