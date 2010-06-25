package org.brzy.squeryl

import org.brzy.validator.Validation
import org.brzy.action.args.Parameters

import javax.validation.{Validation=>jValidation}

import org.squeryl.{KeyedEntity, Schema}
import org.squeryl.PrimitiveTypeMode._

/**
 * Implements the basic CRUD operations on the entity.  The Entity's object companion class
 * should extend this.  Once created, any extra finders should be written into the object class.
 * <pre>
 * object Entity extends SquerylSchema[Long,Entity] {
 *  def findByName(s:String) = ...
 * }
 * </pre>
 */
class SquerylSchema[PK<:AnyVal, T<:KeyedEntity[PK]]()(implicit manifestT: Manifest[T]) extends Schema{
  val db = table[T]
  val validationFactory = jValidation.buildDefaultValidatorFactory

	class EntityCrudOps(t:T) {

    def validate() = Validation[T](validationFactory.getValidator.validate(t))

    def insert() =  db.insert(t)

		def update()= db.update(t)

//    def delete() = db.deleteWhere(e => e.id === t.id)
  }

  implicit def applyCrudOps(t:T) = new EntityCrudOps(t)

//  def get(id:PK) = from(db)(s => where(s.id === id) select(s)).head

  def list() = from(db)(a=> select(a)).toList
//
  def make(params:Parameters):T ={
    // TODO implement me
//		classOf[T].newInstance
    null.asInstanceOf[T]
	}
}