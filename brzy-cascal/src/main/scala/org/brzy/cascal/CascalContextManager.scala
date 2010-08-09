package org.brzy.cascal

import com.shorrockin.cascal.session._
import java.lang.reflect.Method
import util.DynamicVariable
import org.brzy.mvc.interceptor.{ContextFactory, MethodMatcher, ManagedThreadContext}

/**
 * Document Me..
 *
 * @author Michael Fortin
 */
class CascalContextManager extends ManagedThreadContext {
  val hosts = Host("localhost", 9160, 250) :: Nil
  val params = new PoolParams(10, ExhaustionPolicy.Fail, 500L, 6, 2)
  val pool = new SessionPool(hosts, params)

  type T = Option[Session]
  val empty:T = None
  val context = Cascal
  def destroySession(target: Option[Session]) = target.get.close
  def createSession = Some(pool.checkout)
}

/**
 * Document Me..
 */
object Cascal extends DynamicVariable(Option[Session](null))