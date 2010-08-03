package org.brzy.squeryl

import org.brzy.mvc.validator.Validation
import org.brzy.mvc.action.args.Parameters

import javax.validation.{Validation => jValidation}

import org.squeryl.{KeyedEntity, Schema}
import org.squeryl.PrimitiveTypeMode._
import org.brzy.reflect.Construct

/**
 * Implements the basic CRUD operations on the entity.  The Entity's object companion class
 * should extend this.  Once created, any extra finders should be written into the object class.
 * <pre>
 * object Entity extends SquerylPersistence[Entity]  {
 *  def findByName(s:String) = ...
 * }
 * </pre>
 *
 * Also note that the entity class needs to be annotated with the ConstructorProperties
 * annotation for the java.bean api.
 */
class SquerylPersistence[T <: KeyedEntity[Long]]()(implicit manifest: Manifest[T]) extends Schema {
  val db = table[T]
  val validationFactory = jValidation.buildDefaultValidatorFactory

  class EntityCrudOps(t: T) {
    def validate() = Validation[T](validationFactory.getValidator.validate(t))

    def insert() = db.insert(t)

    def update() = db.update(t)

    def delete() = db.deleteWhere(e => e.id === t.id)
  }

  implicit def applyCrudOps(t: T) = new EntityCrudOps(t)

  def get(id: Long):T = from(db)(s => where(s.id === id) select (s)).head

  def list():List[T] = from(db)(a => select(a)).toList

  def make(params: Map[String,Any]): T = Construct[T](params)

}