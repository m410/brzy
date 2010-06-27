package org.brzy.jpa

import org.hibernate.Session
import java.lang.reflect.Method
import util.DynamicVariable
import org.brzy.interceptor.{ContextFactory, MethodMatcher, ManagedThreadContext}
import javax.persistence.{EntityManager, Persistence}

/**
 * Document Me..
 * 
 * @author Michael Fortin
 * @version $Id: $
 */
class JpaThreadManager(unitName:String) extends ManagedThreadContext {

  val entityManagerFactory = Persistence.createEntityManagerFactory(unitName)
  type T = Option[EntityManager]

  val emptyState:T = None
  val matcher = new MethodMatcher {
    def isMatch(a: AnyRef, m: Method) = true
  }
  val context = JpaContext
  val factory = new ContextFactory[T] {
    def destroy(s: T) = s.get.close
    def create = Some(entityManagerFactory.createEntityManager)
  }
}