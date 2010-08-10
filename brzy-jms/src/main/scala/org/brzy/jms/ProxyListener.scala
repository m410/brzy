package org.brzy.jms

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