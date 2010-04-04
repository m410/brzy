package org.brzy.persistence.scalaJpa

import org.brzy.validator.Validity
import org.slf4j.{LoggerFactory, Logger}
import org.brzy.persistence.RichQuery._
import org.brzy.persistence.ThreadScope
import org.brzy.action.args.Parameters

/**
 *	TODO read very helpful http://faler.wordpress.com/2009/08/10/scala-jpa-some-gotchas-to-be-aware-of/
 *
 * another persistence api that may be easier to setup
 * http://max-l.github.com/Squeryl/getting-started.html
 */
abstract class JpaPersistence[T,PK](val clazz:Class[T]) {

  val log:Logger = LoggerFactory.getLogger(clazz.getName)
  val countQuery = "select count(t.id) from " + clazz.getName + " t"
  val listQuery = "select distinct t from " + clazz.getName + " t"

  /**
   * Implicit methods on instances of the entity
   */
  class EntityCrudOps[T](t:T) {

    def validity() ={
      log.trace("validity")
      new Validity()
    }

    def save() = {
      log.trace("save")
      val ctx = ThreadScope.get.asInstanceOf[JpaThreadContext]
      ctx.entityManager.persist(t)
    }

    def delete() = {
      log.trace("delete")
      val ctx = ThreadScope.get.asInstanceOf[JpaThreadContext]
      ctx.entityManager.remove(t)
    }
  }

  implicit def applyCrudOps[T](t:T) = new EntityCrudOps(t)

  def get(id:PK):T = {
    log.trace("get: " + id)
    val ctx = ThreadScope.get.asInstanceOf[JpaThreadContext]
     ctx.entityManager.find(clazz,id)
  }
	
	def count():Long = {
		val ctx = ThreadScope.get.asInstanceOf[JpaThreadContext]
    ctx.entityManager.createQuery(countQuery).getSingleResult.asInstanceOf[Long]
	}
	
	def list():List[T] = {
    val ctx = ThreadScope.get.asInstanceOf[JpaThreadContext]
    ctx.entityManager.createQuery(listQuery).getResultList.toArray.toList.asInstanceOf[List[T]]
	}
	
	def page(start:Int, size:Int):List[T] = {
    val ctx = ThreadScope.get.asInstanceOf[JpaThreadContext]
    ctx.entityManager
        .createQuery(listQuery)
        .setFirstResult(start)
        .setMaxResults(size)
        .getTypedList[T]
	}

	def make(params:Parameters) = {
    var instance = clazz.newInstance
    // set the fields from the params
    instance
	}
}