package $packageName$

import java.beans.ConstructorProperties
import org.brzy.mod.squeryl.SquerylDao

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column

import org.brzy.webapp.controller.{CrudController,Identity}
/**
 * Person Entity
 */
class Person(
    override val id:Long,
    @Column(name="user_name") lar userName:String,
    @Column(length=36) val password:String
    ) extends KeyedEntity[Long] with Ordered[Authority] with Identity {

  def authenticatedRoles:Array[String] = authorities.map(_.name).toArray

  def compareTo(that:Authority) = new CompareToBuilder().append(this.name,that.name).toComparison
  override def toString = userName
  override def hashCode() = new HashCodeBuilder(7,5).append(this.userName).toHashCode
  override def equals(obj:Any) = {
    val that = obj.asInstanceOf[Authority]
    new EqualsBuilder().append(this.name,that.name).isEquals
  }
}

/**
 * $className$ Persistence
 */
object Person extends SquerylDao[Person] {
  val personToAuthorities = oneToManyRelation(Person.db, Authority.db).via((p,a) => p.id === a.personId)
}

class PersonController extends CrudController("persons",Person)