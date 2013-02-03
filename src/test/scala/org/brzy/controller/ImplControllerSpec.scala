package org.brzy.controller

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.junit.Test
import org.brzy.action._
import args.{Arg, Principal, Parameters}
import collection.JavaConversions._
import response.{View, Model}
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class ImplControllerSpec extends WordSpec with ShouldMatchers with Fixtures {

  "A Contoller Implementataion" should {
    "load" in {
//      val controller = new ImplController
//      assertNotNull(controller)
//      assertEquals(2, controller.actions.size)

    }
    "call action" in {
//      val myCtl = new Controller("test") {
//        def fun1(a: Parameters) = ""
//      }
//      implicit val ctlRef = myCtl
//      val action1 = Action("f1", "f1", myCtl.fun1 _)
//      assertNotNull(action1.returnType)
//      assertNotNull(action1.argTypes)
    }
    "call another action" in {
//      val myCtl2 = new Controller("test") {
//        def fun2(a: Parameters) = ""
//      }
//      implicit val ctlRef2 = myCtl2
//      val action2 = Action("f2","f2", myCtl2.fun2 _)
//      assertNotNull(action2.returnType)
//      assertNotNull(action2.argTypes)
    }
    "intercept an action" in {
//
//      val controller = new ImplController
//      val action = controller.actions.find(_.actionPath == "list").get
//      // need to create parameters from
//      val args = Array[Arg](parameters)
//      val result = action.execute(args,principal)
//      assertNotNull(result)
//      assertTrue(result.isInstanceOf[Model])
    }
  }
}


