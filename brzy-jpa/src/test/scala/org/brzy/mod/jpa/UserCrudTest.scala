/*
 * Copyright 2010 Michael Fortin <mike@brzy.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");  you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed 
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR 
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.brzy.mod.jpa

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
      user.insert()
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
      val user = User.getOrElse(0,null)
      assertNotNull(user)
      user.firstName = "Michael"
    }
    ctx.destroySession(session3)

    val session4 = ctx.createSession
    ctx.context.withValue(session4) {
      val user = User.getOrElse(0,null)
      user.delete
    }
    ctx.destroySession(session4)
  }
}