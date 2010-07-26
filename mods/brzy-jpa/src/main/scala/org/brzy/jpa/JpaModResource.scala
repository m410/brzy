package org.brzy.jpa

import org.brzy.config.mod.ModResource
import org.brzy.mvc.interceptor.InterceptorResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class JpaModResource(c:JpaModConfig) extends ModResource with InterceptorResource {
  def interceptor = new JpaContextManager(c.persistenceUnit.get)
  val name = c.name.get

}