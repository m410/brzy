package org.brzy.interceptor.old

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
trait Invoker {
  def invoke(invocation: Invocation): AnyRef = invocation.invoke
}