package org.brzy.action

import args.{Principal, ParametersRequest, Parameters}
import response.{View, Model}

import org.brzy.controller._
import org.brzy.persistence.MockPersistable

import org.springframework.mock.web.{MockRequestDispatcher, MockServletContext, MockHttpServletRequest, MockHttpServletResponse}
import javax.servlet.{ServletResponse, ServletRequest, RequestDispatcher}
import javax.servlet.http.HttpServletResponse



import collection.JavaConversions._
import org.brzy.mock.{MockUserStore, MockUser}


trait Fixtures {


  val params = new Parameters {
    def apply(name: String) = "1"
    def get(name: String) = None
    def url = Map("id"->"1")
    def request = null
    def application = null
    def header = null
    def session = null
    def param = null
    def requestAndUrl = null
  }

  val controller2 = new Controller("") with Authorization {
    override val constraints = List(Roles("ADMIN"))
    override def actions = List(
      get(expr="index", act=index _, view=View("index"),constraints=Seq(Roles("ADMIN","USER"))),
      action("index2", index2 _, View("index2"))
    )
    def index = "name" -> "value"
    def index2 = "name" -> "value"
  }

  class PrincipalMock(n:String, r:Roles) extends Principal {
    def isLoggedIn = true
    def name = n
    def roles = r
  }

  val parameters2 = new Parameters {
    def apply(name: String) = ""
    def get(name: String) = None
    def url = Map.empty[String,String]
    def request = Map.empty[String,Array[String]]
    def application = Map.empty[String,AnyRef]
    def header = Map.empty[String,String]
    def session = None
    def requestAndUrl = Map.empty[String,String]
  }

  val principal = new Principal {
    def isLoggedIn = false
    def name = null
    def roles = null
  }

  val parameters = {
    val map = new collection.mutable.HashMap[String, Array[String]]()
    map.put("lastName",Array("thumb"))
    map.put("firstName",Array("john"))
    map.put("other",Array("yes","no"))

    val jMap = Map("id"->"12321")

    val request = new MockHttpServletRequest(new MockServletContext,"GET", "/users/10")
    request.addParameters(map)

    new ParametersRequest(request, jMap)
  }


  val controller = new Controller("") with Authorization {
    override val constraints = Seq(Roles("ADMIN"))

    override def actions = List(
      get(expr="index0", act=index _, view=View("index0"),constraints=Seq(Ssl(),ContentTypes("text/xml"))),
      get(expr="index1", act=index _, view=View("index1") ,constraints=Seq(Ssl())),
      get(expr="index2", act=index _, view=View("index2") ),
      get(expr="index3", act=index _, view=View("index3") ,constraints=Seq(ContentTypes("text/xml"))),
      get(expr="index4", act=index2 _,view= View("index4"))
    )
    def index = "name" -> "value"
    def index2 = "name" -> "value"
  }

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


  val response = new MockHttpServletResponse()
  val request = new MockHttpServletRequest(new MockServletContext()) {
    override def getRequestDispatcher(path:String):RequestDispatcher = {
      new MockRequestDispatcher(path) {
//        assertEquals("/user/view.ssp",path)
        override def forward( fwdReq:ServletRequest, fwdRes:ServletResponse ) {
          assert(fwdReq.getAttribute("rc") == null) // todo always false
//          assert("Correct rc attribute") // todo always false
        }
      }
    }
    def startAsync() = null
    def startAsync(p1: ServletRequest, p2: ServletResponse) = null
    def isAsyncStarted = false
    def isAsyncSupported = false
    def getAsyncContext = null
    def getDispatcherType = null

    def authenticate(p1: HttpServletResponse) = false
    def login(p1: String, p2: String) {}
    def logout() {}
    def getParts = null
    def getPart(p1: String) = null
  }
}
