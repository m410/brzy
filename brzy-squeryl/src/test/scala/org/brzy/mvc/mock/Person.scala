package org.brzy.mock

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.annotations.Column
import org.squeryl.KeyedEntity
import java.beans.ConstructorProperties
import org.brzy.squeryl.SquerylPersistence
import javax.validation.constraints.{Size, NotNull}
import org.brzy.persistence.Persistent

@ConstructorProperties(Array("id", "firstName", "lastName"))
class Person(override val id: Long,
             @Column(name = "first_name") @NotNull @Size(min = 4, max = 24) val firstName: String,
             @Column(name = "last_name") @NotNull @Size(min = 4, max = 24) val lastName: String)
        extends KeyedEntity[Long] with Persistent[Long] {
  def this() = this (0, "", "")
}

object Person extends SquerylPersistence[Person]