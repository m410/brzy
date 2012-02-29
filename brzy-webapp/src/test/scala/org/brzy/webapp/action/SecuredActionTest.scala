package org.brzy.webapp.action

import args.Principal
import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.webapp.controller.{Authorization, Controller}

class SecuredActionTest extends JUnitSuite {
	
	val controller = new Controller("") with Authorization {
		override val constraints = List(Roles("ADMIN"))
		def actions = List(
      Action("index","index",index _,Roles("ADMIN","USER")),
      Action("index2","index2",index2 _)
    )
		def index = "name" -> "value"
		def index2 = "name" -> "value"
	}

  class PrincipalMock(n:String, r:Roles) extends Principal {
    def isLoggedIn = true
    def name = n
    def roles = r
  }
  
  @Test def testNoPermission() {
		val action = controller.actions(0) 
		assertTrue(action.isAuthorized(new PrincipalMock("me",Roles("USER"))))
	}	

  @Test def testNoRolePermission() {
		val action = controller.actions(1)
		assertFalse(action.isAuthorized(new PrincipalMock("me",Roles("USER"))))
	}

	@Test def testHasPermission() {
		val action = controller.actions(0) 
		assertTrue(action.isAuthorized(new PrincipalMock("me",Roles("ADMIN"))))
	}	
}