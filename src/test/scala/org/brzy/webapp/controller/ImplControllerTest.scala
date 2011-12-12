package org.brzy.webapp.controller

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit.Test
import org.brzy.webapp.action._
import args.{Arg, Principal, Parameters}
import collection.JavaConversions._
import response.{View, Model}

class ImplControllerTest extends JUnitSuite {
  @Test def testLoadController() {
    val controller = new ImplController
    assertNotNull(controller)
    assertEquals(2, controller.actions.size)
//    controller.actions.foreach(a => {
//      println("args:" + a.argTypes.toString)
//      a.argTypes.foreach(t=>{
//        println("*** type:" + t)
//        println("*** class:"+ t.getClass)
//        println("*** methods:"+ t.getClass.getMethods.mkString("[",", ","]"))
//      })
//      println("return:" + a.returnType.toString)
//    })
  }

  @Test def testAction() {
    val myCtl = new Controller("test") {
      val actions:List[Action] = List.empty[Action]
      def fun1(a: Parameters) = ""
    }
    implicit val ctlRef = myCtl
    val action1 = Action("f1", "f1", myCtl.fun1 _)
    assertNotNull(action1.returnType)
    assertNotNull(action1.argTypes)
  }

  @Test def testAction2() {
    val myCtl2 = new Controller("test") {
      val actions:List[Action] = List.empty[Action]
      def fun2(a: Parameters) = ""
    }
    implicit val ctlRef2 = myCtl2
    val action2 = Action("f2","f2", myCtl2.fun2 _)
    assertNotNull(action2.returnType)
    assertNotNull(action2.argTypes)
  }

  @Test def testIntercept() {
    val parameters = new Parameters {
      def apply(name: String) = null
      def url = Map.empty[String,String]
      def request = Map.empty[String,Array[String]]
      def application = Map.empty[String,AnyRef]
      def header = Map.empty[String,String]
      def session = None
    }

    val principal = new Principal {
      def isLoggedIn = false
      def name = null
      def roles = null
    }

    val controller = new ImplController
    val action = controller.actions.find(_.actionPath == "list").get
    // need to create parameters from
    val args = Array[Arg](parameters)
    val result = action.execute(args,principal)
    assertNotNull(result)
    assertTrue(result.isInstanceOf[Model])
  }
}



class ImplController extends Controller("impls") with Secured {
  val actions = Action("list", "list", list _, Roles("SUPER")) ::
          Action("showMore", "show", showMore _) :: Nil

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