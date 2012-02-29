package $packageName$

import org.brzy.webapp.controller.CrudController
import org.brzy.mod.jpa.JpaDao
import reflect.BeanProperty

import java.lang.{Long=>JLong,Integer=>JInt}
import java.util.{List=>JList,Map=>JMap}

import javax.persistence._
import javax.validation.constraints._

/**
 *
 */
@Entity
@Table(name="authority")
class Authority {
  @BeanProperty @Id
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "authority_seq")
  @SequenceGenerator(name="authority_seq", sequenceName = "authority_seq", allocationSize = 1, initialValue= 1)
  var id:JLong = _

  @BeanProperty @NotNull @Size(min=4,max=24) @Column(length=36) var name:String = _
  @BeanProperty @NotNull @Size(min=4,max=24) var description:String = _

  def compareTo(that:Authority) = new CompareToBuilder().append(this.name,that.name).toComparison

  override def toString = description

  override def hashCode() = new HashCodeBuilder(7,5).append(this.name).toHashCode

  override def equals(obj:Any) = {
    val that = obj.asInstanceOf[Authority]
    new EqualsBuilder().append(this.name,that.name).isEquals
  }
}

object Authority extends JpaDao[Authority,JLong]

class AuthorityController extends CrudController("authorities",Authority)