package org.brzy.saved.intercept

/**
 * @author Michael Fortin
 * @version $Id: $
 */
trait InterceptHandler {
  def invoke(invocation: Invocation): AnyRef = invocation.invoke
}