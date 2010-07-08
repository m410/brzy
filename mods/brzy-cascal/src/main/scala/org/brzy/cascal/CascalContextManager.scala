package org.brzy.cascal

import com.shorrockin.cascal.session._
import java.lang.reflect.Method
import util.DynamicVariable
import org.brzy.interceptor.{ContextFactory, MethodMatcher, ManagedThreadContext}

/**
 * Document Me..
 *
 * @author Michael Fortin
 * @version $Id : $
 */
class CascalContextManager extends ManagedThreadContext {
  val hosts = Host("localhost", 9160, 250) :: Nil
  val params = new PoolParams(10, ExhaustionPolicy.Fail, 500L, 6, 2)
  val pool = new SessionPool(hosts, params)

  type T = Option[Session]

  val empty:T = None

  val matcher = new MethodMatcher {
    def isMatch(a: AnyRef, m: Method) = true
  }

  val context = Cascal

  val factory = new ContextFactory[T] {
    def destroy(s: Option[Session]) = s.get.close
    def create = Some(pool.checkout)
  }
}

object Cascal extends DynamicVariable(Option[Session](null))