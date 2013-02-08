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

import javax.validation.ConstraintViolation
import collection.immutable.Set
import org.brzy.beanwrap.{Builder, Editors}
import scala.language.implicitConversions


/**
 * This is a persistent super class that can be used by persistence  modules to enable the
 * use of the Abstract CrudController. It's used by Squeryl Module and the JPA Module.  
 *
 * @author Michael Fortin
 */
trait Dao[T <: {def id: PK}, PK] {

  /**
   * Retrieve the object by it's primary key
   */
  def findBy(id:PK)(implicit pk:Manifest[PK],t:Manifest[T]):T
  
  /**
   * Retrieve a single entity by primary key.
   */
  def getBy(id: PK)(implicit pk:Manifest[PK],t:Manifest[T]): Option[T]

  def getOrElse(id: PK, alternate: T)(implicit pk:Manifest[PK],t:Manifest[T]): T

  /**
   * This is a convenience accessor to an entity, it does the casting, if necessary, from a string
   * to the primary key's type.
   */
  def load(id: String)(implicit pk:Manifest[PK],t:Manifest[T]): T

  /**
   * This implements basic paging.  It does not sort or order the returned list.  To do that
   * you should implement your own list function.
   *
   * @param size the size of the dataset to return
   * @param offset the beginning of the dataset
   */
  def list(size: Int = 50, offset: Int = 0)(implicit t:Manifest[T]): List[T]

  /**
   * Returns a count of the number of entities in the data store.
   */
  def count(implicit t:Manifest[T]): Long

  /**
   * Build the entity from a map of name value pairs.  This depends on the editors setup in the
   * editors repository.
   */
  def construct(map: Map[String, AnyRef])(implicit m: Manifest[T]): T = {
    implicit val implicitEditors = editors
    map.foldLeft(Builder[T]())((a,b)=>{a.set(b._1,b._2)}).make
  }

  /**
   * This creates and instance of the entity using a default no-args constructor.
   */
  def construct()(implicit m: Manifest[T]): T = {
    implicit val implicitEditors = editors
    Builder[T]().make
  }

  /**
   * sets the default editors for building an entity from the request parameters.
   */
  def editors = Editors()

  /**
   * Adds the persistence crud operations to the entity.
   */
  implicit def applyCrudOps(t: T)(implicit m:Manifest[T]): PersistentCrudOps

  /**
   *  Implements the crud operations that are applied directly to instances of persistent
   * objects.  This needs to be created as an implicit value in the companion object.
   */
  abstract class PersistentCrudOps(t: T) {

    /**
     * Insert the entity, with an optional commit immediately parameter.
     */
    def insert(commit: Boolean = false):T

    /**
     * Commit the current transaction.
     */
    def commit()

    /**
     * Update the entity in the db, and return the new instance.
     */
    def update(commit: Boolean = false): T

    /**
     * Delete the entity from the database.
     */
    def delete()

    /**
     * validate the instance.
     */
    def validate: Option[Set[ConstraintViolation[AnyRef]]]
  }

}
