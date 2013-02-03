package org.brzy.controller

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.WordSpec
import org.brzy.action.args.ArgsBuilder

class HomeControllerSpec extends WordSpec with ShouldMatchers with Fixtures {

  "A Controller" should {
    "match path" in {
      val ctlr = new HomeController()
      val action = ctlr.actions.find(_.path == "").get
      assertEquals("/index",action.view)
      assertEquals("/",ArgsBuilder.parseActionPath("/.brzy",""))
      assertTrue(action.isMatch("/","",""))

      val result = action.paramsFor("/")
      assertNotNull(result)
      assertEquals(0, result.size)
    }
  }

}
