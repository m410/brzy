package org.brzy.sample

import org.brzy.persistence.scalaJpa.JpaPersistence
import javax.validation.constraints.{NotNull,Size}
import javax.persistence._

/**
 * @author Michael Fortin
 * @version $Id: $
 */
@serializable
@Entity
@Table(name="persons")
//@NamedQueries({@NamedQuery( name="test", query="select u from User u")})
class Person {

  @Id var id:Long = _
  @Version var version:Int = _
  @Basic var name:String = _

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

object Person extends JpaPersistence[Person,Long](classOf[Person])