package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Assert._
import org.brzy.mock.Person
import org.brzy.mvc.action.args.Parameters
import org.junit.{Ignore, Test}


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