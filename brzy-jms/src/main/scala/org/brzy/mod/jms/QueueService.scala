package org.brzy.mod.jms

import org.brzy.service.Service

/**
 * To create a service that listens to a jms queue, create a service and implement this trait.
 * 
 * @author Michael Fortin
 */
trait QueueService extends Service {

  /**
   * Sets an alternate destination name	defaults to Service's property name
   */
  def destination:String

  /**
   *  Provides a client identifier for durable subscriptions.	defaults to "brzyListener", id
   *  property can be used instead of clientId for backwards compatibility
   */
  def clientId = "brzyListener"

  /**
   * The event listener that relieves messages.
   */
  def onMessage(message:String)
}