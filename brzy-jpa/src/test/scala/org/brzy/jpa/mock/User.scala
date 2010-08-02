package org.brzy.jpa.mock

import org.brzy.jpa.JpaPersistence
import javax.validation.constraints.{NotNull,Size}
import javax.persistence._
import reflect.BeanProperty


@serializable
@Entity
@Table(name="users")
//@NamedQueries()
@NamedQuery(name="test", query="select u from User u")
class User {
  @BeanProperty @Id var id:Long = _
  @BeanProperty @Version var version:Int = _
  @BeanProperty @NotNull @Size(min=4,max=30) var firstName:String = _
  @BeanProperty @NotNull @Size(min=4,max=30) var lastName:String = _

  /*
  @OneToMany{val mappedBy = "project",
             val cascade = Array(CascadeType.ALL),
	     val targetEntity =  classOf[Milestone2],
	     val fetch = FetchType.LAZY }
  var milestones : java.util.List[Milestone2] =
    new java.util.Vector[Milestone2]

     @ManyToMany{val cascade = Array(CascadeType.ALL),
	      val targetEntity =  classOf[User3],
	      val fetch = FetchType.LAZY }
  var users : java.util.List[User3] =
    new java.util.Vector[User3]
  */
}

object User extends JpaPersistence[User,java.lang.Long] 