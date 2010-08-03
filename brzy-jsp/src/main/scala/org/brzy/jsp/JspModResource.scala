package org.brzy.jsp

import org.brzy.config.mod.ModResource
import org.springframework.context.support.ClassPathXmlApplicationContext
import collection.JavaConversions._
import collection.mutable.LinkedHashMap

/**
 * Creates a spring application context and adds all the beans to the application.
 *
 * @see http ://static.springsource.org/spring/docs/3.0.3.RELEASE/spring-framework-reference/html/
 * @author Michael Fortin
 */
class JspModResource(c: JspModConfig) extends ModResource {
  val name = c.name.get
  
}