package org.brzy.squeryl

import org.scalatest.junit.JUnitSuite
import org.junit.Test
import org.junit.Assert._
import org.brzy.mock.Person
import org.brzy.action.args.Parameters


class CrudTest extends JUnitSuite {
  val ctx = new SquerylContextManager("org.h2.Driver", "jdbc:h2:squery-test", "sa", "")

  @Test def testCreate = {
    val session = ctx.factory.create
    ctx.context.withValue(session) {
      def person = new Person(0, "one", "two")
      person.save()
    }
    ctx.factory.destroy(session)
  }

  @Test def testRead = {
    val session = ctx.factory.create
    ctx.context.withValue(session) {
      assertNotNull(Person.get(1))
    }
    ctx.factory.destroy(session)
  }

  @Test def testUpdate = {
    val session = ctx.factory.create
    ctx.context.withValue(session) {
      assertNotNull(Person.get(1))
      val p2 = new Person(1, "update first", "update last")
      p2.update()
    }
    ctx.factory.destroy(session)
  }

  @Test def testDelete = {
    val session = ctx.factory.create
    ctx.context.withValue(session) {
      val person = Person.get(1)
      person.delete()
    }
    ctx.factory.destroy(session)
  }

  @Test def testList = {
    val session = ctx.factory.create
    ctx.context.withValue(session) {
      val persons = Person.list()
      assertNotNull(persons)
      assertEquals(1, persons.size)
    }
    ctx.factory.destroy(session)
  }

  @Test def testMake = {
    val person = Person.make(new Parameters(Map("id" -> 1, "firstName" -> "fred", "lastName" -> "bob")))
    assertNotNull(person)
    assertEquals(1, person.id)
    assertEquals("fred", person.firstName)
    assertEquals("bob", person.lastName)
  }
}