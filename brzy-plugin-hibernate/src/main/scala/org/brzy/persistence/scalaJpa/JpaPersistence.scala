package org.brzy.persistence.scalaJpa

import org.brzy.validator.Validation
import org.slf4j.LoggerFactory
import org.brzy.persistence.RichQuery._
import org.brzy.persistence.ThreadScope
import org.brzy.action.args.Parameters
import javax.validation.{Validator,  Validation}
import java.lang.reflect.Method
import org.brzy.util.ParameterConversion._

/**
 *	TODO read very helpful http://faler.wordpress.com/2009/08/10/scala-jpa-some-gotchas-to-be-aware-of/
 *
 * another persistence api that may be easier to setup
 * http://max-l.github.com/Squeryl/getting-started.html
 */
abstract class JpaPersistence[T <: AnyRef, PK <: AnyRef](val clazz:Class[T]) {

  val log = LoggerFactory.getLogger(clazz.getName)
  val countQuery = "select count(t.id) from " + clazz.getName + " t"
  val listQuery = "select distinct t from " + clazz.getName + " t"

  /**
   * Implicit methods on instances of the entity
   */
  class EntityCrudOps[T](t:T) {

    val validator:Validator = Validation.buildDefaultValidatorFactory.getValidator

    def validity() ={
      log.trace("validity")
      Validation[T](validator.validate(t))
    }

    def save() = {
      log.trace("save")
      val ctx = ThreadScope.get.asInstanceOf[JpaThreadContext]
      ctx.entityManager.persist(t)
    }

    def saveAndCommit() = {
      log.trace("save")
      val ctx = ThreadScope.get.asInstanceOf[JpaThreadContext]
      ctx.entityManager.persist(t)
      ctx.entityManager.getTransaction.commit
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

	def make(params:Parameters):T = {
    log.debug("make with params: {}",params)

    val instance =
      if(params.exists(p => p._1 equals "id"))
        get(toType(classOf[java.lang.Long],(params.get("id").get)(0)).asInstanceOf[PK])
      else
        clazz.newInstance

    params.foreach(p => applyParam(p, instance))
    instance
	}

  private[scalaJpa] def applyParam(nvp:(String, Array[String]), inst:T):Unit = {
    val method:Method = inst.getClass.getMethods.find(mtd => mtd.getName == nvp._1 + "_$eq").orNull

    if(method != null)
      method.invoke(inst,toType(method.getParameterTypes()(0),nvp._2(0)))
  }
}