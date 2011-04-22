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
package org.brzy.mod.jpa.mock

import org.brzy.mod.jpa.JpaDao
import javax.validation.constraints.{NotNull, Size}

import reflect.BeanProperty
import org.hibernate.annotations.NamedQueries
import javax.persistence._
import java.lang.{Long => JLong, Integer => JInt}

@serializable
@Entity
@Table(name = "users")
//@NamedQueries(Array(new NamedQuery(name="list", query="select distinct u from User u")))
class User {
  @BeanProperty @Id
  @GeneratedValue(strategy= GenerationType.SEQUENCE, generator = "user_seq")
  @SequenceGenerator(name="user_seq", sequenceName = "user_seq", allocationSize = 1, initialValue= 1)  
  var id: JLong = _

  @BeanProperty @Version
  var version: JInt = _

  @BeanProperty @NotNull @Size(min = 4, max = 30)
  var firstName: String = _

  @BeanProperty @NotNull @Size(min = 4, max = 30)
  var lastName: String = _
}

object User extends JpaDao[User, JLong] {
  override def construct(p: Map[String, AnyRef])(implicit m:Manifest[User]) = {
    val user = new User()
    user.id = p("id").asInstanceOf[String].toLong
    user.firstName = p("firstName").asInstanceOf[String]
    user.lastName = p("lastName").asInstanceOf[String]
    user
  }
}