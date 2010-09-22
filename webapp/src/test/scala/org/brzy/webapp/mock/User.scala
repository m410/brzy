/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.webapp.mock

import javax.validation.constraints.{NotNull,Size}
//import javax.persistence._

import org.brzy.webapp.action.args.Parameters
import javax.validation.{Validator,  Validation=>xValidation}
import org.brzy.webapp.validator.Validation
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