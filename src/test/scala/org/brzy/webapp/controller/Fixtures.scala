package org.brzy.webapp.controller

import org.brzy.webapp.persistence.MockPersistable
import org.brzy.webapp.action.args.{Principal, Arg, Parameters}

import org.brzy.mock.{MockUserStore, MockUser}
import org.brzy.webapp.action.response.View
import org.brzy.webapp.action.response.Model
import org.brzy.webapp.action.Roles


trait Fixtures {

  class User(
          val id: Long = 0,
          val userName:String = "",
          val password:String = "") extends Authenticated {
    def authenticatedRoles = Array.empty[String]
  }

  trait UserStore extends MockPersistable[User, Long] with Authenticator[User] {
    def login(user:String,pass:String) = None
  }

  class UserController extends CrudController[Long,MockUser]("users") with MockUserStore


  class HomeController extends BaseController("") {
    override val actions = List(action("", index _, View("/index")))
    def index = "hello" -> "world"
  }


  class ImplController extends BaseController("impls") with Authorization {
    override val actions = List(
      get(expr="list", action=list _, view=View("list"), constraints=Seq(Roles("SUPER"))),
      action("showMore", showMore _, View("show") )
    )

    override def intercept(action: () => AnyRef, actionArgs: Array[Arg],principal:Principal) = {
      val paramsOption = actionArgs.find(_.isInstanceOf[Parameters])
      assert(paramsOption.isDefined)
      val result = action()
      assert(result.isInstanceOf[Model])
      result
    }

    def list(p: Parameters) = {
      println("list params")
      Model("name" -> "value")
    }

    def showMore(p: Parameters) = (Model("name" -> "value"), View("/home/other"))
  }



  class SubPathController extends BaseController("/person/{pid}/sub") {
    override val actions = List(
      get("{vid}",view _,View("/person/sub/view"))
    )

    def view(p:Parameters) = "hello" -> "world"
  }
}
