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
import org.brzy.fab.reflect.Construct
import org.brzy.mod.jpa.RichQuery._
import collection.JavaConversions._
import org.brzy.persistence.Dao
import javax.validation.{ConstraintViolation, Validation => jValidation}

/**
 * Implement's the brzy DAO trait for JPA based entities.  This should be implemented by
 * the object companion class to jpa entities.
 * 
 * @author Michael Fortin
 */
class JpaDao[T <:{def id:PK}, PK <: AnyRef]()(implicit man:Manifest[T],pk:Manifest[PK]) extends Dao[T,PK]{
  protected[jpa] val entityClass = man.erasure
  protected[jpa] val keyClass = pk.erasure
  private val log = LoggerFactory.getLogger(entityClass)

  protected[jpa] val validator = jValidation.buildDefaultValidatorFactory.getValidator

  protected[jpa] val countQuery = "select count(t.id) from " + entityClass.getName + " t"
  protected[jpa] val listQuery = "select distinct t from " + entityClass.getName + " t"

  /**
   * Implicit methods on instances of the entity
   */
  class EntityCrudOps[T](t:T) extends PersistentCrudOps(t) {

    protected[jpa] def entityManager = JpaContext.value

    override def validate() ={
      log.trace("validate")
      val set = validator.validate(t).toSet

      if(set.size > 0)
        Option(set.asInstanceOf[Set[ConstraintViolation[AnyRef]]])
      else
        None
    }

    override def delete() {
      log.trace("delete")
      entityManager.remove(t)
    }

    override def insert(commit:Boolean = false) {
      log.trace("insert")

      entityManager.persist(t)

      if(commit) {
        entityManager.getTransaction.commit()
        entityManager.getTransaction.begin()
      }
    }

    override def update():T = {
      log.trace("update")
      entityManager.merge(t)
    }

    override def commit() {
      log.trace("commit")
      entityManager.getTransaction.commit()
    }
  }

  protected[jpa] def entityManager = JpaContext.value

  override def newPersistentCrudOps(t: T) = new EntityCrudOps(t)

  override implicit def applyCrudOps(t: T) = new EntityCrudOps(t)

  def apply(id:PK):T = {
    log.trace("get: " + id)
    entityManager.find(entityClass,id).asInstanceOf[T]
  }

  def get(id:PK):Option[T] = {
    log.trace("get: " + id)
    Option(entityManager.find(entityClass,id).asInstanceOf[T])
  }

  def getOrElse(id:PK,alternate:T):T = {
    val result = entityManager.find(entityClass,id).asInstanceOf[T]
    if(result != null)
      result
    else
      alternate
  }

  protected[jpa] val StringClass = classOf[String]
  protected[jpa] val JIntegerClass = classOf[java.lang.Integer]
  protected[jpa] val JLongClass = classOf[java.lang.Long]
  protected[jpa] val IntClass = classOf[Int]
  protected[jpa] val LongClass = classOf[Long]


  def load(strId:String) = {
    log.trace("get: {}", strId)
    val id = keyClass match {
      case LongClass => strId.toLong
      case JLongClass => java.lang.Long.valueOf(strId)
      case JIntegerClass => java.lang.Integer.valueOf(strId)
      case IntClass => strId.toInt
      case _ => strId
    }
    entityManager.find(entityClass,id).asInstanceOf[T]
  }

  def count() = {
    entityManager.createQuery(countQuery).getSingleResult.asInstanceOf[Long]
	}
	
	def list() = {
    entityManager.createQuery(listQuery).getTypedList[T]
	}
	
	def list(start:Int, size:Int) = {
    entityManager.createQuery(listQuery)
        .setFirstResult(start)
        .setMaxResults(size).getTypedList[T]
	}

}