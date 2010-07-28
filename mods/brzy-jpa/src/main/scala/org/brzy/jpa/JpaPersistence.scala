package org.brzy.jpa

import org.brzy.mvc.validator.Validation
import org.slf4j.LoggerFactory
import org.brzy.mvc.action.args.Parameters
import javax.validation.{Validator,  Validation=>jValidation}
import java.lang.reflect.Method

import org.brzy.util.ParameterConversion._
import org.brzy.jpa.RichQuery._

/**
 *	TODO read very helpful http://faler.wordpress.com/2009/08/10/scala-jpa-some-gotchas-to-be-aware-of/
 *
 * another persistence api that may be easier to setup
 * http://max-l.github.com/Squeryl/getting-started.html
 */
class JpaPersistence[T <: AnyRef, PK <: AnyRef](implicit man:Manifest[T],pk:Manifest[PK]) {
  protected[jpa] val entityClass = man.erasure
  protected[jpa] val keyClass = pk.erasure
  protected[jpa] val log = LoggerFactory.getLogger(entityClass)

  protected[jpa] val validator:Validator = jValidation.buildDefaultValidatorFactory.getValidator

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

    def save() = {
      log.trace("save")
      val entityManager = JpaContext.value.get
      entityManager.persist(t)
    }

    def saveAndCommit() = {
      log.trace("save")
      val entityManager = JpaContext.value.get
      entityManager.persist(t)
      entityManager.getTransaction.commit
    }

    def delete() = {
      log.trace("delete")
      val entityManager = JpaContext.value.get
      entityManager.remove(t)
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
	
	def list():List[T] = {
    val entityManager = JpaContext.value.get
    entityManager.createQuery(listQuery).getResultList.toArray.toList.asInstanceOf[List[T]]
	}
	
	def page(start:Int, size:Int):List[T] = {
    val entityManager = JpaContext.value.get
    entityManager
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
          entityClass.newInstance

    params.foreach(p => applyParam(p, instance.asInstanceOf[T]))
    instance.asInstanceOf[T]
	}

  private[jpa] def applyParam(nvp:(String, Array[String]), inst:T):Unit = {
    val method:Method = entityClass.getMethods.find(mtd => mtd.getName == nvp._1 + "_$eq").orNull

    if(method != null)
      method.invoke(inst,toType(method.getParameterTypes()(0),nvp._2(0)))
  }
}