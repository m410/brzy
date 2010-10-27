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
package org.brzy.persistence

import org.brzy.fab.reflect.Construct
import javax.validation.ConstraintViolation
import collection.immutable.Set

/**
 * This is a persistent super class that can be used by persistence  modules to enable the
 * use of the Abstract CrudController. It's used by Squeryl Module and the JPA Module.  
 * 
 * @author Michael Fortin
 */
trait Persistable[T,PK] {

  /**
   * Retrieve a single entity by primary key.
   */
  def get(id:PK):T 

  /**
   * This is a convenience accessor to an entity, it does the casting, if necessage, from a string
   * to the primary key's data type.
   */
  def load(id:String):T

  /**
   * Returns a list of all entities.
   */
  def list():List[T]

  /**
   * This implements basic paging.  It does not sort or order the returned list.  To do that
   * you should implement your own list function.
   *
   * @param size the size of the dataset to return
   * @param offset the begining of the dataset
   */
  def list(size:Int, offset:Int):List[T]

  /**
   * Returns a count of the number of entities in the data store.
   */
  def count():Long

  /**
   *
   * @See org.brzy.fab.reflect.Construct
   */
  def construct(map:Map[String,Any])(implicit m:Manifest[T]):T = {
    Construct.withCast[T](map.asInstanceOf[Map[String,String]])
  }

  /**
   * This creates and instance of the entity using a default no-args constructor.
   */
  def construct()(implicit m:Manifest[T]):T = Construct[T]()

  /**
   * Used by the abstract CrudController to to add persistence capability to the controller.  I'm
   * sure there's a better way to do this, but for now, this must be implemented in concrete
   * classes.
   */
  def newPersistentCrudOps(t:T):PersistentCrudOps[T]

  /**
   * Adds the persistence crud operations to the entity.
   */
  implicit def applyCrudOps(t:T):PersistentCrudOps[T]

  /**
   *  Implements the crud operations that are applied directly to instances of persistent
   * objects.  This needs to be created as an implicit value in the companion object.
   */
  abstract class PersistentCrudOps[T](t:T) {
    def insert(commit:Boolean = false):Unit
    def commit():Unit
    def update():T
    def delete():Unit
    def validate():Option[Set[ConstraintViolation[T]]]
  }

}
