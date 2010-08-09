package org.brzy.jms

import javax.jms._

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class JmsService(connectionFactory: ConnectionFactory) {

  def send(msg: String, dest:String) = {
    val connection: Connection = connectionFactory.createConnection
    connection.start
    val session: Session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
    val destination: Destination = session.createQueue(dest)

    val producer: MessageProducer = session.createProducer(destination)
    producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT)
    val message: TextMessage = session.createTextMessage(msg)
    producer.send(message)
  }
}