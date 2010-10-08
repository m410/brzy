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
package org.brzy.mod.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.mock.Person
import org.junit.Test


class CrudTest extends JUnitSuite {
  val ctx = new SquerylContextManager("org.h2.Driver", "jdbc:h2:target/squeryl-test", "sa", "")
  
  @Test def testCreate = {

    try {
      val session = ctx.createSession
      ctx.context.withValue(session) {
        Person.create
      }
      ctx.destroySession(session)
    }
    catch {
      case unknown => println("database already exists")
    }

    val session1 = ctx.createSession
    ctx.context.withValue(session1) {
      def person = new Person(0, "one", "two")
      person.insert
    }
    ctx.destroySession(session1)

    val session6 = ctx.createSession
    ctx.context.withValue(session6) {
      assertEquals(1,Person.list.size)
    }
    ctx.destroySession(session6)

    val session2 = ctx.createSession
    ctx.context.withValue(session2) {
      assertNotNull(Person.get(1))
    }
    ctx.destroySession(session2)

    val session3 = ctx.createSession
    ctx.context.withValue(session3) {
      assertNotNull(Person.get(1))
      val p2 = new Person(1, "update first", "update last")
      p2.update
    }
    ctx.destroySession(session3)

    val session4 = ctx.createSession
    ctx.context.withValue(session4) {
      val persons = Person.list()
      assertNotNull(persons)
      assertEquals(1, persons.size)
    }
    ctx.destroySession(session4)

    val session5 = ctx.createSession
    ctx.context.withValue(session5) {
      val person = Person.get(1)
      person.delete
    }
    ctx.destroySession(session5)
  }


  @Test def testMake = {
    val person = Person.construct(Map("id" -> 1, "firstName" -> "fred", "lastName" -> "bob"))
    assertNotNull(person)
    assertEquals(1, person.id)
    assertEquals("fred", person.firstName)
    assertEquals("bob", person.lastName)
  }
}