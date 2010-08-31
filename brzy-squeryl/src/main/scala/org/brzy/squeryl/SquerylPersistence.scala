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
package org.brzy.squeryl

import org.brzy.mvc.validator.Validation
import org.brzy.mvc.action.args.Parameters

import javax.validation.{Validation => jValidation}

import org.squeryl.{KeyedEntity, Schema}
import org.squeryl.PrimitiveTypeMode._
import org.brzy.reflect.Construct
import org.brzy.persistence.{PersistentCrudOps, Persistable}
import java.lang.String
import org.slf4j.LoggerFactory
import reflect.Manifest

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
class SquerylPersistence[T <: KeyedEntity[Long]]()(implicit manifest: Manifest[T]) extends Schema with Persistable[T,Long]{
  val db = table[T]
  val validationFactory = jValidation.buildDefaultValidatorFactory
  private[this] val log = LoggerFactory.getLogger(classOf[SquerylPersistence[_]])
  
  class EntityCrudOps(t: T) extends PersistentCrudOps(t){
    override def validate() = Validation[T](validationFactory.getValidator.validate(t))

    override def insert() = {
      log.trace("insert: {}",t)
      db.insert(t)
    }

    override def update() = db.update(t)

    override def delete() = db.deleteWhere(e => e.id === t.id)
  }

  override def newPersistentCrudOps(t: T) = new EntityCrudOps(t)

  override implicit def applyCrudOps(t: T) = new EntityCrudOps(t)

  def get(id: Long):T = db.lookup(id).get

  def load(id: String) = db.lookup(id.toLong).get

  def list():List[T] = from(db)(a => select(a)).toList

  def list(size:Int, offset:Int):List[T] = from(db)(a => select(a)).toList

}