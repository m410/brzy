package org.brzy.controller

import org.brzy.persistence.MockPersistable
import org.brzy.action.{Roles, Action}
import org.brzy.action.args.{Principal, Arg, Parameters}
import org.brzy.action.response.{View, Model}
import org.junit.Assert._
import org.brzy.mock.{MockUserStore, MockUser}


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

  class UserController extends CrudController[Long,MockUser]("users") with MockUserStore {
    override val actions = super.actions ++ List(
      action("{id}/items/{iid}",sub _,View("itmes"))
    )
    def sub(p:Parameters) = Model("a"->"b")
  }


  class HomeController extends Controller("") {
    override val actions = List(action("", index _, View("/index")))
    def index = "hello" -> "world"
  }


  class ImplController extends Controller("impls") with Authorization {
    override val actions = List(
      get(expr="list", act=list _, view=View("list"), constraints=Seq(Roles("SUPER"))),
      action("showMore", showMore _, View("show") )
    )

    override def intercept(action: () => AnyRef, actionArgs: Array[Arg],principal:Principal) = {
      val paramsOption = actionArgs.find(_.isInstanceOf[Parameters])
      assertTrue(paramsOption.isDefined)
      val result = action()
      assertTrue(result.isInstanceOf[Model])
      result
    }

    def list(p: Parameters) = {
      println("list params")
      Model("name" -> "value")
    }

    def showMore(p: Parameters) = (Model("name" -> "value"), View("/home/other"))
  }



  class SubPathController extends Controller("/person/{pid}/sub") {
    override val actions = List(get("{vid}",view _,View("/person/sub/view")))
    def view(p:Parameters) = "hello" -> "world"
  }
}
