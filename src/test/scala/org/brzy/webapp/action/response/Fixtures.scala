package org.brzy.webapp.action.response

import org.brzy.webapp.action.args.Principal

trait Fixtures {

  class PrincipalMock extends Principal {
    def isLoggedIn = false
    def name = null
    def roles = null
  }

  case class Foo(bar:String)
}
