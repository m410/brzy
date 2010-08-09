package org.brzy.jms

import javax.jms.{Message, JMSException, ExceptionListener, MessageListener}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class ProxyListener(service:AnyRef) extends MessageListener with ExceptionListener {

  def onException(exception: JMSException) = {}

  def onMessage(msg: Message) = {}
}