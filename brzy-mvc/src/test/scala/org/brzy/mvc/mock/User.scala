package org.brzy.mvc.mock

import javax.validation.constraints.{NotNull,Size}
//import javax.persistence._

import org.brzy.mvc.action.args.Parameters
import javax.validation.{Validator,  Validation=>xValidation}
import org.brzy.mvc.validator.Validation
import org.brzy.util.ParameterConversion._

/**
 * @author Michael Fortin
 */
@serializable
//@Entity
//@Table(name="users")
//@NamedQueries({@NamedQuery( name="test", query="select u from User u")})
class User {

//  @Id
  var id:Long = _
//  @Version
  var version:Int = _
  @NotNull @Size(max=30) var name:String = _

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

object User {
	
	class EntityCrudOps[User](t:User) {
		val validator = xValidation.buildDefaultValidatorFactory.getValidator
		def validity() ={
			Validation[User](validator.validate(t))
		}
		def save() = {}
		def saveAndCommit() = {}
		def delete() = {}
	}

	implicit def applyCrudOps[User](t:User) = new EntityCrudOps(t)
	def get(id:Long):User = classOf[User].newInstance
	def count():Long = 0L
	def list():List[User] = List()
	def page(start:Int, size:Int):List[User] = List()
	def make(params:Parameters):User = {
		classOf[User].newInstance
	}
	
}