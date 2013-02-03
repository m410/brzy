package org.brzy.controller

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers
import org.brzy.action.args.ArgsBuilder

class SubPathControllerSpec extends WordSpec with ShouldMatchers with Fixtures {

  "Controller path" should {
    "find action" in {
      val ctlr = new SubPathController()
      val action = ctlr.actions.find(_.path == "{vid}").get
      assertEquals("/person/sub/view",action.view)
      assertEquals("/person/100/sub/200",ArgsBuilder.parseActionPath("/person/100/sub/200.brzy",""))
      assertTrue(action.isMatch("/person/100/sub/200","",""))

      val result = action.paramsFor("/person/100/sub/200")
      assertNotNull(result)
      assertEquals(2, result.size)
      assertEquals("100",result("pid"))
      assertEquals("200",result("vid"))
    }
  }
}
