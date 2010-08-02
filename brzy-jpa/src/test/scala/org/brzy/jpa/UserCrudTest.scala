package org.brzy.jpa

import mock.User
import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.junit.Ignore


class UserCrudTest extends JUnitSuite {
  val ctx = new JpaContextManager(unitName = "jpa-test")

  @Test def testCrud = {

    val session = ctx.createSession
    ctx.context.withValue(session) {
      val user = new User
      user.firstName = "John"
      user.lastName = "Smith"
      user.save
      println("saved: " + user)
    }
    ctx.destroySession(session)

    val session2 = ctx.createSession
    ctx.context.withValue(session2) {
      val list = User.list
      assertNotNull(list)
      assertEquals(1, list.size)
    }
    ctx.destroySession(session2)

    val session3 = ctx.createSession
    ctx.context.withValue(session3) {
      val user = User.get(0)
      assertNotNull(user)
      user.firstName = "Michael"
    }
    ctx.destroySession(session3)

    val session4 = ctx.createSession
    ctx.context.withValue(session4) {
      val user = User.get(0)
      user.delete
    }
    ctx.destroySession(session4)
  }
}