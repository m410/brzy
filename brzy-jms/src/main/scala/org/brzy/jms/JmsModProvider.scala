package org.brzy.jms

import org.brzy.config.mod.ModProvider
import org.apache.activemq.{ActiveMQConnectionFactory, ActiveMQConnection}
import javax.jms.{Session, MessageConsumer, ConnectionFactory}
import org.reflections.scanners.{ResourcesScanner, TypeAnnotationsScanner, SubTypesScanner}
import org.reflections.util.{ConfigurationBuilder, ClasspathHelper}
import org.reflections.Reflections
import collection.mutable.HashMap
import collection.JavaConversions._

/**
 * Document Me..
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