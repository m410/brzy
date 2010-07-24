package org.brzy.d3

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.{Schema, KeyedEntity}
import org.squeryl.annotations.Column
import javax.validation.constraints.{NotNull,Size}
import java.beans.ConstructorProperties
import org.brzy.squeryl.SquerylPersistence

/**
 * @author Michael Fortin
 */
@ConstructorProperties(Array("id","firstName","lastName"))
class Person( override val id:Long,
		@Column(name="first_name") @NotNull @Size(min=4,max=24) val firstName:String,
    @Column(name="last_name") @NotNull @Size(min=4,max=24) val lastName:String) 
		extends KeyedEntity[Long] {
  def this() = this(0, "","")
}

object Person extends SquerylPersistence[Person]