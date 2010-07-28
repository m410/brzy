package org.brzy.spring

import org.brzy.config.mod.ModResource
import org.springframework.context.support.ClassPathXmlApplicationContext
import collection.JavaConversions._

/**
 * Creates a spring application context and adds all the beans to the application.
 *
 * @see http://static.springsource.org/spring/docs/3.0.3.RELEASE/spring-framework-reference/html/
 * @author Michael Fortin
 */
class SpringModResource(c:SpringModConfig) extends ModResource {
  val name = c.name.get
  val context = new ClassPathXmlApplicationContext(c.applicationContext.get)

  override def services = context.getBeanDefinitionNames.map(context.getBean(_).asInstanceOf[AnyRef]).toList
}