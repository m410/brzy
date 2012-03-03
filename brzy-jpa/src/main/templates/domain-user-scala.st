package $packageName$

import org.brzy.webapp.controller.{Identity,CrudController,Authenticator}
import org.brzy.mod.jpa.JpaDao
import org.brzy.beanwrap.{Loader, Editors}

import java.lang.{Long=>JLong,Integer=>JInt,Boolean=>JBoolean}
import java.util.{List=>JList,Map=>JMap,Set=>JSet}

import javax.persistence._
import javax.validation.constraints._
import collection.JavaConversions._
import reflect.BeanProperty

/**
 *
 */
@serializable
@Entity
@Table(name="person")
@NamedQueries(Array(
  new NamedQuery(name="Person.login", query="select p from Person p where userName=:userName and password=:password"),
  new NamedQuery(name="Person.findByUserName", query="select p from Person p where userName=:userName")
))
class Person extends Identity {
  @BeanProperty @Id @GeneratedValue(strategy= GenerationType.AUTO) var id:JLong = _
  @BeanProperty @Version var version:JInt = _
  @BeanProperty @NotNull var name:String = _
  @BeanProperty @NotNull @Column(name="user_name",unique=true, length=24) var userName:String = _
  @BeanProperty @NotNull @Column(length=36) var password:String = _
  @BeanProperty @NotNull var enabled:JBoolean = false

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

class PersonController extends CrudController("persons",Person)  with Authenticator[Person] {
  def findByUserName(name:String) = entityManager.createNamedQuery("Person.findByUserName")
      .setParameter("userName", name)
      .getSingleResult.asInstanceOf[Person]

  def login(user: String, pass: String) = Option(
    try {
      entityManager.createNamedQuery("Person.login")
              .setParameter("userName", user)
              .setParameter("password", pass)
              .getSingleResult.asInstanceOf[Person]
    }
    catch {
      case e: Exception => null
    }
  )
}