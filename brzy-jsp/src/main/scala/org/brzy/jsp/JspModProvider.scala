package org.brzy.jsp

import org.brzy.config.mod.ModProvider

/**
 * Creates a spring application context and adds all the beans to the application.
 *
 * @see http ://static.springsource.org/spring/docs/3.0.3.RELEASE/spring-framework-reference/html/
 * @author Michael Fortin
 */
class JspModProvider(c: JspModConfig) extends ModProvider {
  val name = c.name.get
  
}