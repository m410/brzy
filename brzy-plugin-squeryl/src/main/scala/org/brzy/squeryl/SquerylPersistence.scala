package org.brzy.squeryl

import org.brzy.validator.Validity
import org.squeryl.{Session, Schema}
import org.brzy.action.args.Parameters
import org.slf4j.LoggerFactory
import org.squeryl.PrimitiveTypeMode._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
abstract class SquerylPersistence[T,PK](val clazz:Class[T])(implicit manifestT: Manifest[T]) extends Schema {
  val store = table[T]
  val log = LoggerFactory.getLogger(clazz.getName)

  /**
   * Implicit methods on instances of the entity
   */
  class EntityCrudOps[T](t:T) {

    def validity() ={
      log.trace("validity")
      new Validity()
    }

    def save() = {
      log.trace("save")
//      store.insert(t)
    }

    def delete() = {
      log.trace("delete")
//      persons.deleteWhere(p => p.id === t.id)
    }
  }

  implicit def applyCrudOps[T](t:T) = new EntityCrudOps(t)

//  def get(id:PK):T = {
//    log.trace("get: " + id)
//     from(store)(s => where(s.id === id) select(s)).first
//  }

	def count():Long = {
		val session = Session.currentSession
    from(store)(a=> compute(count))
	}

	def list():List[T] = {
    from(store)(a=> select(a)).toList
	}

	def page(start:Int, size:Int):List[T] = {
    from(store)(a=> select(a)).page(start,size).toList
	}

	def make(params:Parameters) = {
    var instance = clazz.newInstance
    // set the fields from the params
    instance
	}
}