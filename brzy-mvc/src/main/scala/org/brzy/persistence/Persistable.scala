package org.brzy.persistence

import org.brzy.mvc.validator.Validation
import org.brzy.mvc.action.args.Parameters
import org.brzy.reflect.Construct

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

  def construct(params:Parameters)(implicit m:Manifest[T]):T = null.asInstanceOf[T]

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
  def insert():Unit = {}
  def update():Unit = {}
  def delete():Unit = {}
  def validate():Validation[T] = new Validation[T]()
}
