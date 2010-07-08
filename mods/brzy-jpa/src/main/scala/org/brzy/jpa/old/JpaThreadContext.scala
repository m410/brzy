package org.brzy.jpa.old

import javax.persistence.{EntityManager, EntityManagerFactory}
import org.brzy.persistence.ThreadContext

/**
 * @author Michael Fortin
 * @version $Id: $
 */
case class JpaThreadContext(entityManagerFactory:EntityManagerFactory) extends ThreadContext {
  private[this] var entityManagerInternal:EntityManager = _

  def entityManager = {
    if(entityManagerInternal == null) {
      entityManagerInternal = entityManagerFactory.createEntityManager
      entityManagerInternal.getTransaction.begin
    }
    entityManagerInternal
  }

  def start = {
  }
  
  def close = {
    if(entityManagerInternal != null ){

      if(entityManagerInternal.getTransaction.isActive)
        entityManagerInternal.getTransaction.commit

      entityManagerInternal.close
    }
  }	
}