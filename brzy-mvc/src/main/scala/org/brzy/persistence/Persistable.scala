package org.brzy.persistence

import org.brzy.mvc.validator.Validation
import org.brzy.mvc.action.args.Parameters
import org.brzy.reflect.Construct

/**
 * Document Me..
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
  
  implicit def applyCrudOps(t:T) = new PersistentCrudOps(t)

}

class PersistentCrudOps[T](t:T) {
  def insert = {}
  def update = {}
  def delete = {}
  def validate:Validation[T] = new Validation[T]()
}