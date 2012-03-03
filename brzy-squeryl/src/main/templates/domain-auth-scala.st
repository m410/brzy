package $packageName$

import org.brzy.mod.squeryl.SquerylDao

import org.squeryl.PrimitiveTypeMode._
import org.squeryl.KeyedEntity
import org.squeryl.annotations.Column
import org.apache.commons.lang.builder.{EqualsBuilder, HashCodeBuilder, CompareToBuilder}

import org.brzy.webapp.controller.CrudController


/**
 * $className$ Entity
 */
class Authority(
    override val id:Long,
    @Column(name="name") val name:String,
    @Column(name="description") val description:String,
    @Column(name="person_id") val personId:Long
    ) extends KeyedEntity[Long] with Ordered[Authority] {

  lazy val person: ManyToOne[Person] = Person.personToAuthorities.right(this)

  def compareTo(that:Authority) = new CompareToBuilder().append(this.name,that.name).toComparison
  override def toString = description
  override def hashCode() = new HashCodeBuilder(7,5).append(this.name).toHashCode
  override def equals(obj:Any) = {
    val that = obj.asInstanceOf[Authority]
    new EqualsBuilder().append(this.name,that.name).isEquals
  }
}

/**
 * $className$ Persistence
 */
object $className$ extends SquerylDao[$className$]

class $className$Controller extends CrudController("$attributeName$",$className$)