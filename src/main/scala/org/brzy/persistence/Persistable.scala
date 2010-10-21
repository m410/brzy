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

import org.brzy.webapp.validator.Validation
import org.brzy.fab.reflect.Construct

/**
 * This is a persistent super class that can be used by persistence  modules to enable the
 * use of the Abstract CrudController. It's used by Squeryl Module and the JPA Module.  
 * 
 * @author Michael Fortin
 */
trait Persistable[T,PK] {

  def get(id:PK):T 

  def load(id:String):T

  def list():List[T]

  def list(size:Int, offset:Int):List[T]

  def construct(map:Map[String,Any])(implicit m:Manifest[T]):T = Construct[T](map)

  def construct()(implicit m:Manifest[T]):T = Construct[T]()

  /**
   * Used by the abstract CrudController to to add persistence capability to the controller.
   */
  def newPersistentCrudOps(t:T) = new PersistentCrudOps(t)

  implicit def applyCrudOps(t:T) = new PersistentCrudOps(t)
}

/**
 * Implements the crud operations that are applied directly to instances of persistent
 * objects.  This needs to be created as an implicit value in the companion object.
 */
class PersistentCrudOps[T](t:T) {
  def insert() = {}
  def commit() = {}
  def update() = {}
  def delete() = {}
  def validate():Validation[T] = new Validation[T]()
}
