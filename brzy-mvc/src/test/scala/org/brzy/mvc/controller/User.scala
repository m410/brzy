package org.brzy.mvc.controller

import java.beans.ConstructorProperties
import org.brzy.persistence.{MockPersistable, Persistent}

@ConstructorProperties(Array("id"))
class User(val id: Long) extends Persistent[Long] {
  def this() = this(0)
}

object User extends MockPersistable[User, Long]

@Controller("users")
class UserController extends CrudController[User,Long] {
  val persist = User
}

