package org.brzy.jpa

import org.brzy.config.mod.ModProvider
import org.brzy.mvc.interceptor.InterceptorResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class JpaModProvider(c:JpaModConfig) extends ModProvider with InterceptorResource {
  def interceptor = new JpaContextManager(c.persistenceUnit.get)
  val name = c.name.get

}