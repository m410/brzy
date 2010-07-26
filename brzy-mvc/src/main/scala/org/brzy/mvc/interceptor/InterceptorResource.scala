package org.brzy.interceptor

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
trait InterceptorResource {
  def interceptor:ManagedThreadContext

  // TODO add the name of the package scope this interceptor gets applied to later
  // val packageScope:String
}