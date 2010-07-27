package org.brzy.jpa

import org.hibernate.Session
import java.lang.reflect.Method
import util.DynamicVariable
import org.brzy.mvc.interceptor.{ContextFactory, MethodMatcher, ManagedThreadContext}
import javax.persistence.{EntityManager, Persistence}

/**
 * Implements the jps entity manager thread scope variable management.
 * 
 * @author Michael Fortin
 */
class JpaContextManager(unitName:String) extends ManagedThreadContext {
  val entityManagerFactory = Persistence.createEntityManagerFactory(unitName)
  type T = Option[EntityManager]

  val empty:T = None

  val context = JpaContext

  def destroySession(s: T) = s.get.close

  def createSession = Some(entityManagerFactory.createEntityManager)
}