package $packageName$

import org.brzy.webapp.controller.Identity
import org.brzy.webapp.controller.CrudController
import org.brzy.mod.jpa.JpaDao
import reflect.BeanProperty

import java.lang.{Long=>JLong,Integer=>JInt}
import java.util.{List=>JList,Map=>JMap}

import javax.persistence._
import javax.validation.constraints._
import collection.JavaConversions._

/**
 *
 */
@Entity
@Table(name="Person")
class Person extends Identity {
  @BeanProperty @Id
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "person_seq")
  @SequenceGenerator(name="person_seq", sequenceName = "person_seq", allocationSize = 1, initialValue= 1)
  var id:JLong = _

  @BeanProperty @Version var version:JInt = _
  @BeanProperty @NotNull var name:String = _

  @BeanProperty @NotNull @Column(name="user_name",unique=true, length=24) var userName:String = _

  @BeanProperty @NotNull @Column(length=36) var password:String = _

  @BeanProperty @ManyToMany
  @JoinTable(name = "person_authority",
  			joinColumns = Array(new JoinColumn(name = "person_id")),
  			inverseJoinColumns = Array(new JoinColumn(name = "authority_id")))
  var authorities:JSet[Authority] = _

  def authenticatedRoles:Array[String] = authorities.map(_.name).toArray
}

object Person extends JpaDao[Person,JLong] {
  private class IdLoader extends Loader[Person, JLong] {
    override val targetName = Option("id")
    def fromText(t: String) = JLong.valueOf(t)
    def fromStore(i: AnyRef) = {
      val id = i.asInstanceOf[JLong]
      if (id != null && id.longValue > 0) get(id) else None
    }
  }
  override def editors = Editors(List(
    new IdLoader()
  ))
}

class PersonController extends CrudController("persons",Person)