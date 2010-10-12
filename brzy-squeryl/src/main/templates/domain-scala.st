package $packageName$

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import java.beans.ConstructorProperties
import org.brzy.mod.squeryl.SquerylPersistence

/**
 * $className$ Entity
 */
@ConstructorProperties(Array("id","name"))
class $className$(override val id:Long, @Column(name="name") val name:String) extends KeyedEntity[Long]

/**
 * $className$ Persistence
 */
object $className$ extends SquerylPersistence[$className$]