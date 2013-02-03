package org.brzy.action.args

import org.scalatest.{WordSpec, FlatSpec}
import org.springframework.mock.web.{MockServletContext, MockHttpServletRequest}
import org.brzy.action.Action
import org.brzy.controller.Controller
import org.brzy.action.response.Model
import org.scalatest.matchers.ShouldMatchers
import scala.Predef._


class ArgsBuilderSpec extends WordSpec with ShouldMatchers with Fixtures {

  "A ArgsBuilder" should {
    "create parameters" in {
      val request = new MockHttpServletRequest(new MockServletContext())
      val args = ArgsBuilder(request,controller.actions(0))
      assert(args.length == 1)
      assert(args(0).isInstanceOf[Parameters])
    }

    "create parameters and pricipal" in  {
      val request = new MockHttpServletRequest(new MockServletContext())
      val args = ArgsBuilder(request,controller.actions(1))
      assert(args.length == 2)
      assert(args(0).isInstanceOf[Parameters])
      assert(args(1).isInstanceOf[Principal])
    }
  }
}