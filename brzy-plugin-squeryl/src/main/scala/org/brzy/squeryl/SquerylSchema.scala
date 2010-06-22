package org.brzy.squeryl

import org.slf4j.{LoggerFactory, Logger}
import org.brzy.validator.Validation
import javax.validation.Validation
import org.squeryl.{KeyedEntity, Schema}
import org.brzy.action.args.Parameters

/**
 * Implements the basic CRUD operations on the entity.  The Entity's object companion class
 * should extend this.  Once created, any extra finders should be written into the object class.
 * <pre>
 * object Entity extends SquerylSchema[Entity] {
 *  def findByName(s:String) = ...
 * }
 * </pre>
 */
class SquerylSchema[T<:KeyedEntity] extends Schema{
  val log:Logger = LoggerFactory.getLogger(classOf[T])
  val db = table[T]
  val validationFactory = Validation.buildDefaultValidatorFactory

	class EntityCrudOps(t:T) {

    def validate() ={
      log.trace("validity")
      Validation[T](validationFactory.getValidator.validate(t))
    }

    def save() = {
      log.trace("save")
			db.insert(t)
    }

		def update():Unit = {
      log.trace("save")
			db.update(t)
    }

    def delete() = {
      log.trace("delete")
			db.deleteWhere(db => db.id === t.id)
    }
  }

  implicit def applyCrudOps(t:T) = new EntityCrudOps(t)

  def get(id:Long) = from(db)(s => where(s.id === id) select(s)).head

  def list() = from(db)(a=> select(a)).toList

  def make(params:Parameters):T ={
    // TODO implement me
		classOf[T].newInstance
	}
}