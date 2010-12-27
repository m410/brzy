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

import org.slf4j.LoggerFactory
import javax.jms._
import collection.JavaConversions._

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class ProxyListener(service:AnyRef) extends MessageListener with ExceptionListener {
  val log = LoggerFactory.getLogger(classOf[ProxyListener])
  val annotation = service.getClass.getAnnotation(classOf[Queue])
  val onMessageMethod = service.getClass.getMethod(annotation.listenerMethod, classOf[String])

  def onException(exception: JMSException) = {
    log.error("Exception Receiving Message:",exception)
  }

  def onMessage(msg: Message) = {
    log.trace("Receiving Message: {}",msg)

    msg match {
      case t:TextMessage => onMessageMethod.invoke(service,t.getText)
      case o:ObjectMessage => error("ObjectMesages are not Implemented yet")
      case _ => error("Unknown Message Type: " + msg)
    }
  }
}