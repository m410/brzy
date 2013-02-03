package org.brzy.action.response

import org.brzy.action.args.Principal

trait Fixtures {

  class PrincipalMock extends Principal {
    def isLoggedIn = false
    def name = null
    def roles = null
  }

  case class Foo(bar:String)
}
