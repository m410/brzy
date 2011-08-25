package org.brzy.webapp.action

import org.junit.Test
import org.junit.Assert._
import org.scalatest.junit.JUnitSuite
import org.brzy.webapp.controller.{Secured, Controller}

class SecuredActionTest extends JUnitSuite {
	
	val controller = new Controller("") with Secured {
		override val roles = Roles("ADMIN","USER")
		def actions = List(Action("index","index",index _,Roles("ADMIN")))
		def index = "name" -> "value"
	}
	
  @Test def testNoPermission() {
		val action = controller.actions(0) 
		assertTrue(!action.authorize(Principal("me",Roles("USER"))))
	}	
	
	@Test def testHasPermission() {
		val action = controller.actions(0) 
		assertTrue(action.authorize(Principal("me",Roles("ADMIN"))))
	}	
  
}