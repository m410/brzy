package org.brzy.cascal

import org.brzy.config.mod.ModProvider
import org.brzy.mvc.interceptor.InterceptorResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 */
class CascalModProvider(c:CascalModConfig) extends ModProvider with InterceptorResource{
  val name = c.name.get
  def interceptor = new CascalContextManager
}