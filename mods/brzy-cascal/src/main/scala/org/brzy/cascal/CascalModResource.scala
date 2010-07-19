package org.brzy.cascal

import org.brzy.config.mod.ModResource
import org.brzy.interceptor.InterceptorResource

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class CascalModResource(c:CascalModConfig) extends ModResource with InterceptorResource{
  val name = c.name.get
  def interceptor = new CascalContextManager
}