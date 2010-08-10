package org.brzy.spring

import org.brzy.config.mod.ModProvider
import org.springframework.context.support.ClassPathXmlApplicationContext
import collection.JavaConversions._
import collection.mutable.LinkedHashMap

/**
 * Creates a spring application context and adds all the beans to the application.
 *
 * @see http ://static.springsource.org/spring/docs/3.0.3.RELEASE/spring-framework-reference/html/
 * @author Michael Fortin
 */
class SpringModProvider(c: SpringModConfig) extends ModProvider {
  val name = c.name.get
  val context = new ClassPathXmlApplicationContext(c.applicationContext.get)

  override val serviceMap = {
    val map = LinkedHashMap[String, AnyRef]()
    context.getBeanDefinitionNames.foreach(x => map += x -> context.getBean(x).asInstanceOf[AnyRef])
    map.toMap
  }
}