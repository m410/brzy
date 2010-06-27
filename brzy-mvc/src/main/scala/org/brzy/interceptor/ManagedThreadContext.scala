package org.brzy.interceptor

import util.DynamicVariable

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

trait ManagedThreadContext {
  type T <: AnyRef
  val factory: ContextFactory[T]
  val context: DynamicVariable[T]
  val matcher: MethodMatcher
  val emptyState: T
}