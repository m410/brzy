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

import org.apache.activemq.{ActiveMQConnectionFactory, ActiveMQConnection}
import javax.jms.{Session, ConnectionFactory}
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.Reflections
import collection.mutable.HashMap
import collection.JavaConversions._
import org.brzy.fab.mod.ModProvider

/**
 * Module provider for JMS.  This will scan for services that implement the QueueService Trait.
 *
 * @author Michael Fortin
 */
class JmsModProvider(c: JmsModConfig) extends ModProvider {
  val name = c.name.get

  val connectionFactory: ConnectionFactory = new ActiveMQConnectionFactory(
    if(c.userName.isEmpty) ActiveMQConnection.DEFAULT_USER else c.userName.get,
    if(c.password.isEmpty) ActiveMQConnection.DEFAULT_PASSWORD else c.password.get,
    if(c.brokerUrl.isEmpty) ActiveMQConnection.DEFAULT_BROKER_URL else c.brokerUrl.get)

  private val reflections = new Reflections(new ConfigurationBuilder()
          .setUrls(ClasspathHelper.getUrlsForPackagePrefix(c.scanPackage.get))
          .setScanners(
    new ResourcesScanner(),
    new TypeAnnotationsScanner(),
    new SubTypesScanner()))

  override val serviceMap = {
    val services = asSet(reflections.getTypesAnnotatedWith(classOf[Queue]))
    val map = HashMap[String, AnyRef]()
    services.foreach(s=> {
      val instance = s.newInstance.asInstanceOf[AnyRef]
      val className = s.getSimpleName
      map += ( {className.charAt(0).toLower + className.substring(1,className.length)} -> instance)
    })
    map.toMap
  }

  override def startup = {
    val connection = connectionFactory.createConnection
    connection.start

    serviceMap.values.foreach(service => {
      val anno = service.getClass.getAnnotation(classOf[Queue])
      val listener = new ProxyListener(service)
      val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
      val destination = session.createQueue(anno.destination)
      val consumer = session.createConsumer(destination)
      connection.setExceptionListener(listener)
      consumer.setMessageListener(listener)
    })
  }
}