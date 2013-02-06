package org.brzy.action


import org.scalatest.WordSpec
import org.scalatest.matchers.ShouldMatchers

class SecuredActionSpec extends WordSpec with ShouldMatchers with Fixtures {

  "A Secure Action" should {
    "deny action" in {
      val action = controller2.actions(1)
      assert(!action.isAuthorized(new PrincipalMock("me",Roles("USER"))))
    }
    "accept action" in {
      val action = controller2.actions(0)
      assert(action.isAuthorized(new PrincipalMock("me",Roles("ADMIN"))))
    }
  }
}