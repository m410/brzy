/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.mod.jpa

import javax.persistence.{EntityManager, Persistence}
import org.brzy.fab.interceptor.ManagedThreadContext
import org.slf4j.LoggerFactory
import util.DynamicVariable

/**
 * Implements the jps entity manager thread scope variable management.
 * 
 * @author Michael Fortin
 */
class JpaContextManager(unitName:String) extends ManagedThreadContext {
  private[this] val log = LoggerFactory.getLogger(getClass())
  val entityManagerFactory = Persistence.createEntityManagerFactory(unitName)
  type T = Option[EntityManager]

  val empty:T = None

  val context = JpaContext

  def createSession = {
    val entityManager:EntityManager = entityManagerFactory.createEntityManager
    entityManager.getTransaction.begin
    log.trace("create session: {}",entityManager)
    Some(entityManager)

  }

  def destroySession(s: T) = {
    log.trace("destroy session: {}",s)
    s.get.getTransaction.commit
    s.get.close
  }
}

/**
 * Thread local context that holds the the entity manager.
 *
 * @author Michael Fortin
 */
object JpaContext extends DynamicVariable(Option[EntityManager](null))