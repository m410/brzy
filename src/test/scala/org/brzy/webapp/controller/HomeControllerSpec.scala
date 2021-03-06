package org.brzy.webapp.controller

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec
import org.brzy.webapp.action.args.ArgsBuilder


class HomeControllerSpec extends WordSpec with ShouldMatchers with Fixtures {

  "A Controller" should {
    "match path" in {
      val ctlr = new HomeController()
      val action = ctlr.actions.find(_.path == "").get
//      assert("/index".equalsIgnoreCase(action.view))
      val actionPath = ArgsBuilder.parseActionPath("/.brzy", "")
      assert("/".equalsIgnoreCase(actionPath.path))
      assert(actionPath.isServlet)
      assert(action.isMatch("GET","","/"))

      val result = action.paramsFor("/")
      assert(result != null)
      assert(0 == result.size)
    }
  }

}
