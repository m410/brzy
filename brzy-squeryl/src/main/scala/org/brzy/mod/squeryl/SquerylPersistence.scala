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
package org.brzy.mod.squeryl

import org.squeryl.PrimitiveTypeMode._
import java.lang.String
import org.slf4j.LoggerFactory
import reflect.Manifest
import org.brzy.validator.Validator
import org.squeryl.{Session, KeyedEntity, Schema}
import javax.validation.ConstraintViolation
import collection.immutable.Set
import org.brzy.persistence.Persistable

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
class SquerylPersistence[T <: KeyedEntity[Long]]()(implicit manifest: Manifest[T])
        extends Schema with Persistable[T,Long]{
  val db = table[T]
  private[this] val log = LoggerFactory.getLogger(classOf[SquerylPersistence[_]])
  
  class EntityCrudOps(t: T) extends PersistentCrudOps(t){
    def validate = valid(t)

    def insert(commit:Boolean = false) = {
      log.trace("insert: {}",t)
      db.insert(t)
    }

    def update = {
      db.update(t)
      t
    }

    def commit = Session.currentSession.connection.commit

    def delete() = db.deleteWhere(e => e.id === t.id)
  }

  /**
   * conveniences function used by the implicit operations on the entity
   */
  def valid(t:T) = Validator(t).violations

  implicit def applyCrudOps(t: T) = new EntityCrudOps(t)

  def newPersistentCrudOps(t: T) = new EntityCrudOps(t)

  def get(id: Long):Option[T] = db.lookup(id)

  def getOrElse(id: Long, alternate:T) =  db.lookup(id) match {
    case Some(e) => e
    case _ => alternate
  }


  def load(id: String) = db.lookup(id.toLong).get

  def list():List[T] = from(db)(a => select(a)).toList

  def list(size:Int, offset:Int):List[T] = from(db)(a => select(a)).toList

  def count = from(db)(a => compute(countDistinct(a.id)))
}