package org.brzy.webapp.action

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.webapp.controller.{Secured, Controller}

class SecuredActionTest extends JUnitSuite {
	
	val controller = new Controller("") with Secured {
		override val constraints = List(Roles("ADMIN"))
		def actions = List(
      Action("index","index",index _,Roles("ADMIN","USER")),
      Action("index2","index2",index2 _)
    )
		def index = "name" -> "value"
		def index2 = "name" -> "value"
	}
	
  @Test def testNoPermission() {
		val action = controller.actions(0) 
		assertTrue(action.isAuthorized(Principal("me",Roles("USER"))))
	}	

  @Test def testNoRolePermission() {
		val action = controller.actions(1)
		assertFalse(action.isAuthorized(Principal("me",Roles("USER"))))
	}

	@Test def testHasPermission() {
		val action = controller.actions(0) 
		assertTrue(action.isAuthorized(Principal("me",Roles("ADMIN"))))
	}	
}