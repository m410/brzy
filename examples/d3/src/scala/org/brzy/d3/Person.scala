package org.brzy.d3

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.annotations.Column
import org.brzy.action.args.Parameters
import org.brzy.validator.{Validation=>BValidation}
import javax.validation.{ConstraintViolation,Validation,Validator,ValidatorFactory}
import org.slf4j.{LoggerFactory, Logger}

/**
 * @author Michael Fortin
 * @version $Id: $
 */
class Person( override val id:Long,
		@Column(name="first_name") val firstName:String,
    @Column(name="last_name") val lastName:String) 
		extends KeyedEntity[Long] {
  def this() = this(0, "","")
}


object Person extends Schema {
  val log:Logger = LoggerFactory.getLogger(classOf[Person])
  val persons = table[Person]
	
	class EntityCrudOps(t:Person) {

    def validate() ={
      log.trace("validity")
			val factory = Validation.buildDefaultValidatorFactory
			val validator = factory.getValidator
			val constraintViolations = validator.validate(t)
      BValidation(constraintViolations)
    }

    def save() = {
      log.trace("save")
			persons.insert(t)
    }

		def update():Unit = {
      log.trace("save")
			persons.update(t)
    }

    def delete() = {
      log.trace("delete")
			persons.deleteWhere(db => db.id === t.id)
    }
  }

  implicit def applyCrudOps(t:Person) = new EntityCrudOps(t)

  def get(id:Long) = from(persons)(s => where(s.id === id) select(s)).head
	def make(params:Parameters):Person ={
		val person = classOf[Person].newInstance
		person
	}
}