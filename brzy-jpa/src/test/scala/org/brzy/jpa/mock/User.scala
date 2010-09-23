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
package org.brzy.jpa.mock

import org.brzy.jpa.JpaPersistence
import javax.validation.constraints.{NotNull,Size}
import javax.persistence._
import reflect.BeanProperty
import java.beans.ConstructorProperties
import org.brzy.webapp.action.args.Parameters


@serializable
@Entity
@Table(name="users")
@NamedQueries(Array(new NamedQuery(name="list", query="select distinct u from User u")))
@NamedQuery(name="test", query="select u from User u")
@ConstructorProperties(Array("id","version","firstName","lastName"))
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

object User extends JpaPersistence[User,java.lang.Long] {
  override def make(p: Parameters) = {
    val user = new User()
    user.id= p("id").toLong
    user.version = 1
    user.firstName = p("firstName")
    user.lastName = p("lastName")
    user
  }
}