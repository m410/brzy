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

import org.slf4j.LoggerFactory
import javax.validation.{Validation=>jValidation}

import org.brzy.webapp.validator.Validation
import org.brzy.fab.reflect.Construct

/**
 *	TODO read very helpful http://faler.wordpress.com/2009/08/10/scala-jpa-some-gotchas-to-be-aware-of/
 *
 * another persistence api that may be easier to setup
 * http://max-l.github.com/Squeryl/getting-started.html
 */
class JpaPersistence[T <: AnyRef, PK <: AnyRef]()(implicit man:Manifest[T],pk:Manifest[PK]) {
  protected[jpa] val entityClass = man.erasure
  protected[jpa] val keyClass = pk.erasure
  private val log = LoggerFactory.getLogger(entityClass)

  protected[jpa] val validator = jValidation.buildDefaultValidatorFactory.getValidator

  protected[jpa] val countQuery = "select count(t.id) from " + entityClass.getName + " t"
  protected[jpa] val listQuery = "select distinct t from " + entityClass.getName + " t"

  /**
   * Implicit methods on instances of the entity
   */
  class EntityCrudOps[T](t:T) {

    def validate() ={
      log.trace("validate")
      Validation[T](validator.validate(t))
    }

    def save = {
      log.trace("save")
      val entityManager = JpaContext.value.get
      entityManager.persist(t)
    }

    def delete = {
      log.trace("delete")
      val entityManager = JpaContext.value.get
      entityManager.remove(t)
    }

    def insert = {
      log.trace("delete")
      val entityManager = JpaContext.value.get
      entityManager.persist(t)
    }

    def update = {
      log.trace("delete")
      val entityManager = JpaContext.value.get
      entityManager.persist(t)      
    }
  }

  implicit def applyCrudOps[T](t:T) = new EntityCrudOps(t)

  def get(id:PK):T = {
    log.trace("get: " + id)
    val entityManager = JpaContext.value.get
    entityManager.find(entityClass,id).asInstanceOf[T]
  }
	
	def count():Long = {
		val entityManager = JpaContext.value.get
    entityManager.createQuery(countQuery).getSingleResult.asInstanceOf[Long]
	}
	
	def list():java.util.List[T] = {
    val entityManager = JpaContext.value.get
    entityManager.createQuery(listQuery).getResultList.asInstanceOf[java.util.List[T]]
	}
	
	def list(start:Int, size:Int):java.util.List[T] = {
    val entityManager = JpaContext.value.get
    entityManager.createQuery(listQuery)
        .setFirstResult(start)
        .setMaxResults(size).getResultList.asInstanceOf[java.util.List[T]]
	}

	def construct(params:Map[String,Any]):T = {
    log.debug("make with params: {}",params)
    Construct.withCast[T](params.asInstanceOf[Map[String,String]])
	}
}