package org.brzy.jms

import org.brzy.config.mod.ModProvider
import org.apache.activemq.{ActiveMQConnectionFactory, ActiveMQConnection}
import javax.jms.{Session, MessageConsumer, ConnectionFactory}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class JmsModProvider(c: JmsModConfig) extends ModProvider {
  val name = c.name.get

  val connectionFactory: ConnectionFactory = new ActiveMQConnectionFactory(
    ActiveMQConnection.DEFAULT_USER,
    ActiveMQConnection.DEFAULT_PASSWORD,
    ActiveMQConnection.DEFAULT_BROKER_URL)


  override val serviceMap = Map[String,AnyRef]()

  override def startup = {
    val connection = connectionFactory.createConnection
    connection.start

    serviceMap.values.foreach(service => {
      val listener = new ProxyListener(service)
      val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
      val destination = session.createQueue("mmy first active mq queue")
      val consumer = session.createConsumer(destination)
      connection.setExceptionListener(listener)
      consumer.setMessageListener(listener)
    })
  }
}