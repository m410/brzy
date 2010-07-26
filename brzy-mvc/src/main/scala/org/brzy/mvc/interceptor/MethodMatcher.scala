package org.brzy.mvc.interceptor

import java.lang.reflect.Method

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */

trait MethodMatcher {
  def isMatch(a: AnyRef, m: Method): Boolean
}