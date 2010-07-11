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
  def interceptor = new CascalContextManager
}