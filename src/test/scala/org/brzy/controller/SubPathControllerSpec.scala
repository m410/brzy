package org.brzy.controller


import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.action.args.ArgsBuilder


class SubPathControllerSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Controller path" should {
    "find action" in {
      val ctlr = new SubPathController()
      val action = ctlr.actions.find(_.path == "{vid}").get
//      assert("/person/sub/view".equalsIgnoreCase(action.view))
      assert("/person/100/sub/200".equalsIgnoreCase(ArgsBuilder.parseActionPath("/person/100/sub/200.brzy","")))
      assert(true == action.isMatch("/person/100/sub/200","",""))

      val result = action.paramsFor("/person/100/sub/200")
      assert(result != null)
      assert(2 == result.size)
      assert("100".equalsIgnoreCase(result("pid")))
      assert("200".equalsIgnoreCase(result("vid")))
    }
  }
}
