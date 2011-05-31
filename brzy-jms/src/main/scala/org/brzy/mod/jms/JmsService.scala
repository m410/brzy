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
package org.brzy.mod.jms

import javax.jms._

/**
 * A Service that can be injected into other services and controllers to send jms messages.
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